package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.NlsSafe
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.elementType
import com.intellij.util.IncorrectOperationException
import org.lukasj.idea.torquescript.psi.TSNamedElement
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.reference.TSGlobalVarReference

abstract class TSVarExpressionElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), TSNamedElement {
    override fun getReference(): PsiReference? =
        when (firstChild.elementType) {
            TSTypes.GLOBALVAR -> TSGlobalVarReference(this, TextRange(0, firstChild.textLength))
            TSTypes.LOCALVAR -> null
            TSTypes.THISVAR -> null
            else -> throw NotImplementedError("The element '${firstChild.text}' with type ${firstChild.elementType} is not handled")
        }

    override fun getNameIdentifier(): PsiElement? = this

    override fun getName(): String? =
        text

    override fun setName(name: String): PsiElement {
        TODO("Not yet implemented")
    }
}