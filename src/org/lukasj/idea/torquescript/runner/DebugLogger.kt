package org.lukasj.idea.torquescript.runner

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project

interface DebugLogger {
    fun print(text: String, consoleType: LogConsoleType, contentType: ConsoleViewContentType)
    fun println(text: String, consoleType: LogConsoleType, contentType: ConsoleViewContentType)
    fun printHyperlink(text: String, consoleType: LogConsoleType, handler: (project: Project) -> Unit)
    fun error(text: String, consoleType: LogConsoleType = LogConsoleType.DEBUGGER)
}

enum class LogConsoleType {
    DEBUGGER, NORMAL
}
