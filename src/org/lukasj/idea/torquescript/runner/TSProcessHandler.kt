package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.util.io.BaseOutputReader

class TSProcessHandler(commandLine: GeneralCommandLine)
    : KillableColoredProcessHandler(commandLine) {
    override fun readerOptions(): BaseOutputReader.Options =
        BaseOutputReader.Options.forMostlySilentProcess()
}
