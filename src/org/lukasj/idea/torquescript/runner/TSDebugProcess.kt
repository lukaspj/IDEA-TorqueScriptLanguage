package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Processor
import com.intellij.util.containers.ContainerUtil
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointManager
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.impl.XSourcePositionImpl
import org.jetbrains.annotations.Nullable
import org.jetbrains.debugger.BreakpointManager
import org.lukasj.idea.torquescript.TSFileUtil
import java.io.BufferedReader
import java.io.File
import java.io.PrintWriter
import java.nio.channels.AsynchronousSocketChannel
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class TSDebugProcess(debugSession: XDebugSession) : XDebugProcess(debugSession), DebugLogger {
    private val configuration: TSRunConfiguration = session.runProfile as TSRunConfiguration
    private var processHandler: TSProcessHandler? = null
    private var isStopped: Boolean = false
    private var outputThread: Thread? = null
    private var breakpointThread: Thread? = null
    private var movedBreakpointThread: Thread? = null
    private var telnetClient: TSTelnetClient? = null

    override fun getEditorsProvider(): XDebuggerEditorsProvider =
        TSDebuggerEditorsProvider()

    override fun sessionInitialized() {
        val debugMain = TSFileUtil.getPluginVirtualFile("scripts/debuggermain.tscript")
        println(debugMain)

        println(configuration.workingDir)
        val commandLine = GeneralCommandLine(configuration.appPath)
        commandLine.addParameters(debugMain)

        val dir = configuration.workingDir
        commandLine.workDirectory = File(dir!!)

        try {
            processHandler = TSProcessHandler(commandLine)

            val debugLogger = this
            processHandler!!.addProcessListener(object : ProcessListener {
                override fun startNotified(processEvent: ProcessEvent) {

                }

                override fun processTerminated(processEvent: ProcessEvent) {
                    if (!isStopped)
                        session.stop()
                }

                override fun processWillTerminate(processEvent: ProcessEvent, b: Boolean) {

                }

                override fun onTextAvailable(processEvent: ProcessEvent, key: Key<*>) {
                    if (key === ProcessOutputTypes.STDOUT) {
                        debugLogger.print(
                            processEvent.text,
                            LogConsoleType.NORMAL,
                            ConsoleViewContentType.NORMAL_OUTPUT
                        )
                    } else if (key === ProcessOutputTypes.STDERR) {
                        debugLogger.error(processEvent.text)
                    }
                }
            })
            processHandler!!.startNotify()

            telnetClient = TSTelnetClient("127.0.0.1", 17432)
            telnetClient!!.connect()

            outputThread = thread {
                while (!isStopped) {
                    val line = telnetClient!!.outputQueue.poll(200, TimeUnit.MILLISECONDS)
                    if (line != null) {
                        println(line, LogConsoleType.NORMAL, ConsoleViewContentType.LOG_INFO_OUTPUT)
                    }
                }
            }

            movedBreakpointThread = thread {
                while (!isStopped) {
                    val movedBreakpointEvent = telnetClient!!.movedBreakpointQueue.poll(200, TimeUnit.MILLISECONDS)
                    if (movedBreakpointEvent != null) {
                        val file = findFile(movedBreakpointEvent.file)
                        if (file != null) {
                            val resolvedBp = getBreakpoint(file, movedBreakpointEvent.line)
                            if (resolvedBp != null) {
                                val breakpointManager = XDebuggerManager.getInstance(session.project).breakpointManager
                                WriteCommandAction.runWriteCommandAction(session.project) {
                                    breakpointManager.removeBreakpoint(resolvedBp)
                                    if (movedBreakpointEvent.newLine != null) {
                                        breakpointManager.addLineBreakpoint(
                                            TSLineBreakpointType(),
                                            file.url,
                                            movedBreakpointEvent.newLine,
                                            (resolvedBp.type as TSLineBreakpointType).createBreakpointProperties(
                                                file,
                                                movedBreakpointEvent.newLine
                                            )
                                        )
                                    }
                                }
                            } else {
                                print(
                                    "Debugger error, failed to resolve BP (${file.name}:${movedBreakpointEvent.line})",
                                    LogConsoleType.DEBUGGER,
                                    ConsoleViewContentType.LOG_WARNING_OUTPUT
                                )
                            }
                        }
                    }
                }
            }

            breakpointThread = thread {
                while (!isStopped) {
                    val stackLines = telnetClient!!.breakpointQueue.poll(200, TimeUnit.MILLISECONDS)?.stackLines
                    if (stackLines != null) {
                        val file = findFile(stackLines[0].file)
                        if (file != null) {
                            val resolvedBp = getBreakpoint(file, stackLines[0].line)
                            val suspendContext = TSSuspendContext(
                                TSExecutionStack(stackLines
                                    .filter { it.file != "<none>" }
                                    .mapIndexed { idx, stackLine ->
                                        TSStackFrame(
                                            session.project,
                                            findSourcePosition(stackLine.file, stackLine.line)!!,
                                            stackLine.function,
                                            idx,
                                            telnetClient!!
                                        )
                                    }
                                ))
                            if (resolvedBp == null) {
                                session.positionReached(suspendContext)
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
                }
            }

            telnetClient!!.login("password")

            sendAllBreakpoints()

            telnetClient!!.eval("setMainDotCsDir(\"${dir.replace('\\', '/')}\");")
            telnetClient!!.eval("setCurrentDirectory(\"${dir.replace('\\', '/')}\");")
            telnetClient!!.eval("echo(\"Hello From IntelliJ!\");")
            telnetClient!!.eval("exec(\"main.tscript\");")
            telnetClient!!.resume()
        } catch (e: Exception) {
            e.message?.let { error(it) }
            session.stop()
        }
    }

    private fun findFile(file: String): VirtualFile? = VfsUtil.findFile(
        Path.of(configuration.appPath!!).parent
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
        if (processHandler != null && processHandler!!.canKillProcess()) {
            processHandler!!.killProcess()
        }
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
                        registerBreakpoint(sourcePosition, breakpoint)
                    }
                }

                override fun unregisterBreakpoint(
                    breakpoint: XLineBreakpoint<XBreakpointProperties<*>>,
                    temporary: Boolean
                ) {
                    val sourcePosition = breakpoint.sourcePosition
                    if (sourcePosition != null) {
                        unregisterBreakpoint(sourcePosition, breakpoint)
                    }
                }
            }
        )

    fun registerBreakpoint(sourcePosition: XSourcePosition, breakpoint: XLineBreakpoint<*>) =
        telnetClient?.setBreakpoint(
            File(sourcePosition.file.path).relativeTo(File(configuration.appPath!!).parentFile).path,
            sourcePosition.line,
            false,
            0
        )

    fun unregisterBreakpoint(sourcePosition: XSourcePosition, breakpoint: XLineBreakpoint<*>) =
        telnetClient?.clearBreakpoint(
            File(sourcePosition.file.path).relativeTo(File(configuration.appPath!!).parentFile).path,
            sourcePosition.line
        )

    fun sendAllBreakpoints() {
        if (telnetClient != null) {
            processBreakpoint { bp ->
                bp.sourcePosition?.let { registerBreakpoint(it, bp) }
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
        print("$text\n", consoleType, ConsoleViewContentType.ERROR_OUTPUT)

    override fun startPausing() =
        telnetClient!!.pause()

    override fun resume(context: XSuspendContext?) =
        telnetClient!!.resume()

    override fun startStepOver(context: XSuspendContext?) =
        telnetClient!!.stepOver()

    override fun startStepInto(context: XSuspendContext?) =
        telnetClient!!.stepIn()

    override fun startStepOut(context: XSuspendContext?) =
        telnetClient!!.stepOut()
}