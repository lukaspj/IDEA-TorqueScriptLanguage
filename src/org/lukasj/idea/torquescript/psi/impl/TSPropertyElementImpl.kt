package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.psi.*
import org.lukasj.idea.torquescript.reference.*

abstract class TSPropertyElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), TSNamedElement, TSProperty {
    override fun getReference(): PsiReference? {
        // Determine how this property is used
        // First resolve the "parent"
        var parentRef: PsiReference? = null
        if (parent is TSQualifierAccessor) {
            if (parent.prevSibling is TSIdentExpression) {
                parentRef = parent.prevSibling.reference
            } else if (parent.prevSibling is TSVarExpression) {
                parentRef = parent.prevSibling.reference
            }
        }

        // Is it a method call?
        if (parentRef != null && nextSibling != null && nextSibling is TSAccessorChain && nextSibling.firstChild is TSCallAccessor) {
            return when (parentRef) {
                is TSObjectReference ->
                    return TSFunctionReference(this, TextRange(0, textLength), parentRef.element.name, text)
                is TSGlobalVarReference ->
                    return ReferenceUtil.tryResolveType(parentRef.element)
                        ?.let {
                            TSFunctionReference(this, TextRange(0, textLength), it, text)
                        }
                is TSLocalVarReference ->
                    return ReferenceUtil.tryResolveType(parentRef.element)
                        ?.let {
                            TSFunctionReference(this, TextRange(0, textLength), it, text)
                        }
                else -> null
            }
        }

        return null
    }

    override fun getReferences(): Array<PsiReference> =
        reference?.let { arrayOf(it) }
            ?: arrayOf()

    override fun getIdentifyingElement() = this

    override fun getNameIdentifier() = this

    override fun getName() = this.text

    override fun setName(name: String): PsiElement {
        this.firstChild.replace(TSElementFactory.createSimple<PsiElement>(project, name))
        return this
    }
}