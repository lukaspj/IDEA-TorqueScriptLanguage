package org.lukasj.idea.torquescript.runner

import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XSuspendContext

class TSSuspendContext(private val event: TSExecutionStack): XSuspendContext() {
    override fun getActiveExecutionStack(): XExecutionStack = event
}