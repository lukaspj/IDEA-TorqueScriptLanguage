package org.lukasj.idea.torquescript.runner

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Processor
import com.intellij.util.containers.ContainerUtil
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.impl.XSourcePositionImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.telnet.TSTelnetClient
import java.io.File
import java.nio.file.Path

class TSDebugProcess(
    val host: String,
    val port: Int,
    val password: String,
    debugSession: XDebugSession,
    val initFn: ((TSTelnetClient) -> Unit)? = null
) :
    XDebugProcess(debugSession), DebugLogger {
    private var isStopped: Boolean = false
    private var outputThread: Thread? = null
    private val scope = CoroutineScope(Job() + Dispatchers.Default)
    private var telnetClient: TSTelnetClient? = null
    private var targetPosition: XSourcePosition? = null
    private var workingDirectory: String = TSFileUtil.getRootDirectory(debugSession.project)

    override fun getEditorsProvider(): XDebuggerEditorsProvider =
        TSDebuggerEditorsProvider()

    override fun sessionInitialized() {
        try {
            telnetClient = TSTelnetClient(host, port)
            telnetClient!!.connect()

            scope.launch {
                telnetClient!!.output.consumeAsFlow()
                    .collect {
                        println(it, LogConsoleType.NORMAL, ConsoleViewContentType.LOG_INFO_OUTPUT)
                    }
            }

            scope.launch {
                telnetClient!!.movedBreakpoints.consumeAsFlow()
                    .collect { bpEvent ->
                        val file = findFile(bpEvent.file)
                        val resolvedBp = file?.let { getBreakpoint(it, bpEvent.line) }
                        if (resolvedBp != null) {
                            val breakpointManager = XDebuggerManager.getInstance(session.project).breakpointManager
                            WriteCommandAction.runWriteCommandAction(session.project) {
                                breakpointManager.removeBreakpoint(resolvedBp)
                                if (bpEvent.newLine != null) {
                                    breakpointManager.addLineBreakpoint(
                                        TSLineBreakpointType(),
                                        file.url,
                                        bpEvent.newLine,
                                        (resolvedBp.type as TSLineBreakpointType).createBreakpointProperties(
                                            file,
                                            bpEvent.newLine
                                        )
                                    )
                                }
                            }
                        } else {
                            print(
                                "Debugger error, failed to resolve moved BP (${bpEvent.file}:${bpEvent.line})",
                                LogConsoleType.DEBUGGER,
                                ConsoleViewContentType.LOG_WARNING_OUTPUT
                            )
                        }
                    }
            }

            scope.launch {
                telnetClient!!.breakpoints.consumeAsFlow()
                    .map { it.stackLines }
                    .collect { stackLines ->
                        val sourcePosition = findSourcePosition(stackLines[0].file, stackLines[0].line)
                        val file = findFile(stackLines[0].file)
                        val resolvedBp = file?.let { getBreakpoint(it, stackLines[0].line) }
                        val suspendContext = TSSuspendContext(
                            TSExecutionStack(stackLines
                                .mapIndexed { idx, stackLine ->
                                    Pair(idx, stackLine)
                                }
                                .filter { it.second.file != "<none>" }
                                .map { idxStackline ->
                                    TSStackFrame(
                                        session.project,
                                        findSourcePosition(idxStackline.second.file, idxStackline.second.line),
                                        idxStackline.second.function,
                                        idxStackline.first,
                                        telnetClient!!
                                    )
                                }
                            ))
                        if (resolvedBp == null) {
                            session.positionReached(suspendContext)
                            if (targetPosition != null
                                && targetPosition!!.file == sourcePosition?.file
                                && targetPosition!!.line == sourcePosition.line
                            ) {
                                targetPosition = null
                                unregisterBreakpoint(sourcePosition)
                            } else {
                                print(
                                    "Debugger error, failed to resolve triggered BP (${stackLines[0].file}:${stackLines[0].line})",
                                    LogConsoleType.DEBUGGER,
                                    ConsoleViewContentType.LOG_WARNING_OUTPUT
                                )
                            }
                        } else {
                            session.breakpointReached(
                                resolvedBp,
                                null,
                                suspendContext
                            )
                        }
                        ApplicationManager.getApplication()
                            .invokeLater {
                                session.showExecutionPoint()
                            }
                    }
            }

            scope.launch {
                val loginResult = telnetClient!!.login(password)
                if (loginResult.isFailure) {
                    error("Debugger was unable to login to TelNet Server - ${loginResult.exceptionOrNull()?.message}")
                    session.stop()
                    return@launch
                }

                initFn?.invoke(telnetClient!!)

                workingDirectory = telnetClient!!.evalAtLevel(0, "getMainDotCsDir()")

                sendAllBreakpoints()
                telnetClient!!.resume()
            }
        } catch (e: Exception) {
            e.message?.let { error("An unexpected error occured in the IntelliJ debugger: $it \n ${e.stackTraceToString()}") }
            session.stop()
        }
    }

    private fun findFile(file: String): VirtualFile? = VfsUtil.findFile(
        Path.of(workingDirectory)
            .resolve(Path.of(file)),
        false
    )

    private fun findSourcePosition(file: String, line: Int): XSourcePosition? {
        val virtualFile = findFile(file)
        return if (virtualFile != null) {
            XSourcePositionImpl.create(virtualFile, line)
        } else {
            null
        }
    }

    override fun stop() {
        isStopped = true
        if (outputThread != null) {
            outputThread!!.join()
        }
        telnetClient!!.disconnect()
    }

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> =
        arrayOf(
            object : XBreakpointHandler<XLineBreakpoint<XBreakpointProperties<*>>>(TSLineBreakpointType::class.java) {
                override fun registerBreakpoint(breakpoint: XLineBreakpoint<XBreakpointProperties<*>>) {
                    val sourcePosition = breakpoint.sourcePosition
                    if (sourcePosition != null) {
                        registerBreakpoint(sourcePosition, breakpoint.conditionExpression?.toString())
                    }
                }

                override fun unregisterBreakpoint(
                    breakpoint: XLineBreakpoint<XBreakpointProperties<*>>,
                    temporary: Boolean
                ) {
                    val sourcePosition = breakpoint.sourcePosition
                    if (sourcePosition != null) {
                        unregisterBreakpoint(sourcePosition)
                    }
                }
            }
        )

    fun registerBreakpoint(sourcePosition: XSourcePosition, condition: String? = null) =
        runBlocking {
            telnetClient?.setBreakpoint(
                File(sourcePosition.file.path).relativeTo(File(workingDirectory)).path.replace('\\', '/'),
                sourcePosition.line,
                false,
                0,
                condition ?: "true"
            )
        }

    fun unregisterBreakpoint(sourcePosition: XSourcePosition) =
        runBlocking {
            telnetClient?.clearBreakpoint(
                File(sourcePosition.file.path).relativeTo(File(workingDirectory)).path,
                sourcePosition.line
            )
        }

    fun sendAllBreakpoints() {
        if (telnetClient != null) {
            processBreakpoint { bp ->
                bp.sourcePosition?.let {
                    registerBreakpoint(
                        it,
                        bp.conditionExpression?.toString()
                    )
                }
                true
            }
        }
    }

    fun processBreakpoint(processor: Processor<XLineBreakpoint<*>>) {
        ApplicationManager.getApplication().runReadAction {
            val breakpoints = XDebuggerManager.getInstance(session.project)
                .breakpointManager
                .getBreakpoints(TSLineBreakpointType::class.java)
            ContainerUtil.process(breakpoints, processor)
        }
    }

    fun getBreakpoint(file: VirtualFile, line: Int): XLineBreakpoint<*>? {
        var bp: XLineBreakpoint<*>? = null
        processBreakpoint {
            val pos = it.sourcePosition
            if (file == pos?.file && line == pos.line) {
                bp = it
            }
            true
        }
        return bp
    }

    override fun print(text: String, consoleType: LogConsoleType, contentType: ConsoleViewContentType) =
        session.consoleView.print(text, contentType)

    override fun println(text: String, consoleType: LogConsoleType, contentType: ConsoleViewContentType) =
        print("$text\n", consoleType, contentType)

    override fun printHyperlink(text: String, consoleType: LogConsoleType, handler: (project: Project) -> Unit) =
        session.consoleView.printHyperlink(text, handler)

    override fun error(text: String, consoleType: LogConsoleType) =
        print("\n$text\n", consoleType, ConsoleViewContentType.ERROR_OUTPUT)

    override fun startPausing() =
        runBlocking {
            telnetClient!!.pause()
        }

    override fun resume(context: XSuspendContext?) =
        runBlocking {
            telnetClient!!.resume()
        }

    override fun startStepOver(context: XSuspendContext?) =
        runBlocking {
            telnetClient!!.stepOver()
        }

    override fun startStepInto(context: XSuspendContext?) =
        runBlocking {
            telnetClient!!.stepIn()
        }

    override fun startStepOut(context: XSuspendContext?) =
        runBlocking {
            telnetClient!!.stepOut()
        }

    override fun runToPosition(position: XSourcePosition, context: XSuspendContext?) {
        targetPosition = position
        val bp = getBreakpoint(position.file, position.line)
        if (bp == null) {
            registerBreakpoint(position)
        }

        session.resume()
    }
}

