package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase
import org.lukasj.idea.torquescript.TSFileType

class TSLineBreakpointType : XLineBreakpointTypeBase(ID, NAME, TSDebuggerEditorsProvider()) {

    override fun canPutAt(file: VirtualFile, line: Int, project: Project): Boolean =
        file.fileType === TSFileType.INSTANCE

    companion object {
        val ID = "ts-line"
        val NAME = "ts-line-breakpoint"
    }
}