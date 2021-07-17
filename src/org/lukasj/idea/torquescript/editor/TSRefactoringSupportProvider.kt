package org.lukasj.idea.torquescript.editor

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.lukasj.idea.torquescript.psi.TSNamedElement

class TSRefactoringSupportProvider : RefactoringSupportProvider() {
    override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
        when (element) {
            is TSNamedElement -> true
            else -> false
        }
}