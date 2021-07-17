package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProviderBase
import org.lukasj.idea.torquescript.TSFileType
import org.lukasj.idea.torquescript.psi.impl.TSExprCodeFragmentImpl

class TSDebuggerEditorsProvider : XDebuggerEditorsProviderBase() {
    override fun getFileType() = TSFileType.INSTANCE

    override fun createExpressionCodeFragment(
        project: Project,
        text: String,
        context: PsiElement?,
        isPhysical: Boolean
    ): PsiFile {
        val fragment = TSExprCodeFragmentImpl(
            project,
            "fragment.tscript",
            text,
            isPhysical
        )
        fragment.context = context
        return fragment
    }


}