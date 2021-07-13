package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.reference.TSFunctionCallReference
import org.lukasj.idea.torquescript.reference.TSNamespaceReference
import org.lukasj.idea.torquescript.reference.TSObjectReference

abstract class TSIdentExpressionElementImpl(node: ASTNode) : ASTWrapperPsiElement(node) {
    override fun getReference(): PsiReference? {
        if(firstChild == null) {
            throw NotImplementedError("The element '${firstChild.text}' with type ${firstChild.elementType} is not handled")
        }

        if (firstChild != lastChild) {
            return null
        }

        if (firstChild.elementType == TSTypes.IDENT)
            return TSObjectReference(firstChild, TextRange(0, firstChild.textLength))

        throw NotImplementedError("The element '${firstChild.text}' with type ${firstChild.elementType} is not handled")
    }

    override fun getReferences(): Array<PsiReference> {
        if (firstChild != lastChild) {
            return arrayOf(TSObjectReference(firstChild, TextRange(0, firstChild.textLength)))
        }
        val ref = reference
        return if (ref != null) {
            arrayOf(ref)
        } else {
            arrayOf()
        }
    }
}