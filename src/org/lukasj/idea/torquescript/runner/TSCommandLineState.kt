package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment

class TSCommandLineState(private val configuration: TSRunConfiguration, environment: ExecutionEnvironment) :
    CommandLineState(environment) {

    override fun startProcess(): ProcessHandler =
        TSProcessHandler(
            GeneralCommandLine(listOf(configuration.appPath))
                .withWorkDirectory(configuration.workingDirectory)
        )
}
