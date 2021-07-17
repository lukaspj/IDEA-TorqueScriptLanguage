package org.lukasj.idea.torquescript.runner

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager

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
                return TSDebugProcess(xDebugSession)
            }
        })
    }
}