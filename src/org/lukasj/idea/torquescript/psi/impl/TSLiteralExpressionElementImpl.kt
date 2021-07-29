package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.lukasj.idea.torquescript.reference.TSFileReference

abstract class TSLiteralExpressionElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), PsiElement {
    override fun getReference(): PsiReference? {
        val value = text.trim()

        return when {
            value.contains("/") ->
                TSFileReference(this, TextRange(1, value.length - 1))
            else -> null
        }
    }

    override fun getReferences(): Array<PsiReference> =
        reference.let {
            if (it != null) {
                arrayOf(it)
            } else {
                arrayOf()
            }
        }
}