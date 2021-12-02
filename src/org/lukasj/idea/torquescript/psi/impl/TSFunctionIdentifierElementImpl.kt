package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.lukasj.idea.torquescript.psi.TSElementFactory
import org.lukasj.idea.torquescript.psi.TSIdentExpression
import org.lukasj.idea.torquescript.psi.TSNamedElement
import org.lukasj.idea.torquescript.reference.TSFunctionReference
import org.lukasj.idea.torquescript.reference.TSObjectReference

abstract class TSFunctionIdentifierElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TSNamedElement {
    override fun getNameIdentifier(): PsiElement =
        lastChild

    override fun getName(): String? = nameIdentifier.text

    override fun setName(name: String): PsiElement {
        nameIdentifier.replace(TSElementFactory.createSimple<TSIdentExpressionImpl>(project, name))
        return this
    }

    override fun getReference(): PsiReference? =
        references.last()

    override fun getReferences(): Array<PsiReference> =
        if (firstChild != lastChild) {
            arrayOf(
                TSObjectReference(this, TextRange(0, firstChild.textLength)),
                TSFunctionReference(this, TextRange(0, lastChild.textLength))
            )
        } else {
            arrayOf(
                TSFunctionReference(this, TextRange(0, firstChild.textLength)),
            )
        }
}