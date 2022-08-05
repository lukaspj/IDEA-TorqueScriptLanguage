package org.lukasj.idea.torquescript.runner

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.Key
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebugSessionListener
import com.intellij.xdebugger.XDebuggerManager
import kotlinx.coroutines.runBlocking
import org.lukasj.idea.torquescript.TSFileUtil
import java.io.File

// Note: https://github.com/EmmyLua/IntelliJ-EmmyLua
class TSRunner : GenericProgramRunner<RunnerSettings>() {
    override fun canRun(executorId: String, profile: RunProfile) =
        profile is TSRunConfiguration &&
                DefaultDebugExecutor.EXECUTOR_ID == executorId || DefaultRunExecutor.EXECUTOR_ID == executorId

    override fun getRunnerId() = "ts.t3d.runner"

    @Throws(ExecutionException::class)
    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        FileDocumentManager.getInstance().saveAllDocuments()

        // debug
        if (environment.executor.id == DefaultDebugExecutor.EXECUTOR_ID) {
            val session: XDebugSession = createSession(environment)
            return session.runContentDescriptor
        }

        // execute
        val result = state.execute(environment.executor, environment.runner)
        if (result != null) {
            val builder = RunContentBuilder(result, environment)
            return builder.showRunContent(environment.contentToReuse)
        }
        return null
    }

    @Throws(ExecutionException::class)
    private fun createSession(environment: ExecutionEnvironment): XDebugSession {
        val manager = XDebuggerManager.getInstance(environment.project)
        return manager.startSession(environment, object : XDebugProcessStarter() {
            @Throws(ExecutionException::class)
            override fun start(xDebugSession: XDebugSession): XDebugProcess {
                val configuration: TSRunConfiguration = environment.runProfile as TSRunConfiguration

                val debugProcess =
                    TSDebugProcess("127.0.0.1", 17432, "password", xDebugSession) {
                        runBlocking {
                            val dir = configuration.workingDirectory
                            it.eval("setMainDotCsDir(\"${dir.replace('\\', '/')}\");")
                            it.eval("setCurrentDirectory(\"${dir.replace('\\', '/')}\");")
                            it.eval("echo(\"Hello From IntelliJ!\");")
                            it.eval("exec(\"${configuration.mainScript}\");")
                        }
                    }
                val debugLogger = debugProcess

                val debugMain = TSFileUtil.getPluginVirtualFile("scripts/debuggermain.tscript")

                val commandLine = GeneralCommandLine(configuration.appPath)
                commandLine.addParameters(debugMain)

                val dir = configuration.workingDirectory
                commandLine.workDirectory = File(dir)
                val processHandler = TSProcessHandler(commandLine)

                processHandler.addProcessListener(object : ProcessListener {
                    override fun startNotified(processEvent: ProcessEvent) {

                    }

                    override fun processTerminated(processEvent: ProcessEvent) {
                        if (!xDebugSession.isStopped) {
                            ApplicationManager.getApplication()
                                .invokeLater {
                                    xDebugSession.stop()
                                }
                        }
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
                processHandler.startNotify()

                xDebugSession.addSessionListener(object : XDebugSessionListener {
                    override fun sessionStopped() {
                        if (processHandler.canKillProcess()) {
                            processHandler.killProcess()
                        }

                        super.sessionStopped()
                    }
                })

                return debugProcess
            }
        })
    }
}