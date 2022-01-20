package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.psi.*
import org.lukasj.idea.torquescript.reference.TSFunctionCallReference
import org.lukasj.idea.torquescript.reference.TSFunctionReference
import org.lukasj.idea.torquescript.reference.TSNamespaceReference
import org.lukasj.idea.torquescript.reference.TSObjectReference

abstract class TSIdentExpressionElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), TSNamedElement {
    override fun getReference(): PsiReference? {
        if(firstChild == null) {
            throw NotImplementedError("The element '${firstChild.text}' with type ${firstChild.elementType} is not handled")
        }

        if (parent.elementType == TSTypes.CALL_EXPRESSION) {
            return null
        }

        if (firstChild != lastChild) {
            return TSObjectReference(this, firstChild.textRangeInParent)
        }

        if (firstChild.elementType == TSTypes.IDENT)
            return TSObjectReference(this, firstChild.textRangeInParent)

        if (firstChild.elementType == TSTypes.IDENT_EXPRESSION)
            return TSObjectReference(this, TextRange(0, firstChild.firstChild.textLength))

        throw NotImplementedError("The element '${firstChild.text}' with type ${firstChild.elementType} is not handled")
    }

    override fun getReferences(): Array<PsiReference> {
        if (firstChild != lastChild) {
            return arrayOf(
                TSObjectReference(this, firstChild.textRangeInParent),
                TSFunctionReference(this, textRangeInParent, this.text)
            )
        }
        val ref = reference
        return if (ref != null) {
            arrayOf(ref)
        } else {
            arrayOf()
        }
    }

    override fun getNameIdentifier() = this.lastChild

    override fun getName() = this.nameIdentifier.text

    override fun setName(name: String): PsiElement {
        this.lastChild.replace(TSElementFactory.createSimple<TSIdentExpressionImpl>(project, name))
        return this
    }
}