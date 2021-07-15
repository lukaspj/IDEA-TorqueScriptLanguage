package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler

class TSProcessHandler(commandLine: GeneralCommandLine)
    : KillableColoredProcessHandler(commandLine) {
}
