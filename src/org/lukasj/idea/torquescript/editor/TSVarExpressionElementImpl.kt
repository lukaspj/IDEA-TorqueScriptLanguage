package org.lukasj.idea.torquescript.editor

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.psi.TSElementFactory
import org.lukasj.idea.torquescript.psi.TSNamedElement
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.reference.TSGlobalVarReference
import org.lukasj.idea.torquescript.reference.TSLocalVarReference

abstract class TSVarExpressionElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), TSNamedElement {
    override fun getReference(): PsiReference? =
        when (firstChild.elementType) {
            TSTypes.GLOBALVAR -> TSGlobalVarReference(this, TextRange(0, firstChild.textLength))
            TSTypes.LOCALVAR -> TSLocalVarReference(this, TextRange(0, firstChild.textLength))
            TSTypes.THISVAR -> TSLocalVarReference(this, TextRange(0, firstChild.textLength))
            else -> throw NotImplementedError("The element '${firstChild.text}' with type ${firstChild.elementType} is not handled")
        }

    override fun getNameIdentifier(): PsiElement? = this

    override fun getName(): String? =
        text

    override fun setName(name: String): PsiElement {
        return replace(TSElementFactory.createSimple(project, name))
    }
}