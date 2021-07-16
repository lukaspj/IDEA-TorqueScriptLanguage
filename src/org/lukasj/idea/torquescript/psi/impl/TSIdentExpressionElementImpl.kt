package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.psi.*
import org.lukasj.idea.torquescript.reference.TSFunctionCallReference
import org.lukasj.idea.torquescript.reference.TSNamespaceReference
import org.lukasj.idea.torquescript.reference.TSObjectReference

abstract class TSIdentExpressionElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), TSNamedElement {
    override fun getReference(): PsiReference? {
        if(firstChild == null) {
            throw NotImplementedError("The element '${firstChild.text}' with type ${firstChild.elementType} is not handled")
        }

        if (firstChild != lastChild) {
            return TSObjectReference(this, TextRange(0, firstChild.textOffset))
        }

        if (firstChild.elementType == TSTypes.IDENT)
            return TSObjectReference(this, TextRange(0, textLength))

        if (firstChild.elementType == TSTypes.IDENT_EXPRESSION)
            return TSObjectReference(this, TextRange(0, firstChild.firstChild.textLength))

        throw NotImplementedError("The element '${firstChild.text}' with type ${firstChild.elementType} is not handled")
    }

    override fun getReferences(): Array<PsiReference> {
        if (firstChild != lastChild) {
            return arrayOf(
                TSObjectReference(this, TextRange(0, firstChild.textLength)),
                TSObjectReference(this, TextRange(firstChild.textLength+2, firstChild.textLength+2+lastChild.textLength))
            )
        }
        val ref = reference
        return if (ref != null) {
            arrayOf(ref)
        } else {
            arrayOf()
        }
    }

    override fun getIdentifyingElement() = this

    override fun getNameIdentifier() = this

    override fun getName() = this.text

    override fun setName(name: String): PsiElement {
        this.lastChild.replace(TSElementFactory.createIdent(project, name))
        return this
    }
}