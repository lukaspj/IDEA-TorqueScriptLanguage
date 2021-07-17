package org.lukasj.idea.torquescript.runner

import com.intellij.execution.ExecutionResult
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.DebuggableRunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import org.jetbrains.concurrency.Promise

class TSCommandLineState(private val configuration: TSRunConfiguration, environment: ExecutionEnvironment) :
    CommandLineState(environment), DebuggableRunProfileState {

    override fun startProcess(): ProcessHandler =
        TSProcessHandler(
            GeneralCommandLine(listOf(configuration.appPath))
        )

    override fun execute(debugPort: Int): Promise<ExecutionResult> {
        TODO("Not yet implemented")
    }
}
