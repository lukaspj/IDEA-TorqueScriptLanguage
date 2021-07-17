package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import com.intellij.refactoring.suggested.startOffset
import org.lukasj.idea.torquescript.psi.*
import org.lukasj.idea.torquescript.reference.TSFunctionCallReference
import org.lukasj.idea.torquescript.reference.TSFunctionReference
import org.lukasj.idea.torquescript.reference.TSObjectReference

abstract class TSFunctionCallExpressionElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TSNamedElement {
    abstract fun getCallAccessor(): TSCallAccessor
    abstract fun getExpression(): TSExpression

    override fun getNameIdentifier(): PsiElement? =
        when (functionType) {
            TSFunctionType.METHOD -> {
                if (getExpression() is TSIdentExpression) {
                    getExpression()
                } else {
                    null
                }
            }
            TSFunctionType.GLOBAL -> {
                firstChild
            }
            TSFunctionType.GLOBAL_NS -> {
                if (getExpression() is TSIdentExpression) {
                    getExpression()
                } else {
                    null
                }
            }
        }

    override fun getName(): String? =
        nameIdentifier?.text

    override fun setName(name: String): PsiElement {
        var identifier = nameIdentifier ?: return this
        if (nameIdentifier is TSIdentExpression) {
            identifier = nameIdentifier?.lastChild!!
        }
        identifier.replace(TSElementFactory.createSimple<TSIdentExpressionImpl>(project, name))
        return this
    }

    val functionType: TSFunctionType
        get() {
            val expression = getExpression()

            if (expression.elementType == TSTypes.IDENT_EXPRESSION) {
                return if (expression.node.findChildByType(TSTypes.COLON_COLON) != null) {
                    TSFunctionType.GLOBAL_NS
                } else {
                    TSFunctionType.GLOBAL
                }
            }

            // It's an expression, we have no way of knowing what it might evaluate to..
            return TSFunctionType.METHOD
        }

    override fun getReference(): PsiReference? {
        val identifier = nameIdentifier ?: return null
        return when (functionType) {
            TSFunctionType.GLOBAL -> {
                TSFunctionReference(this, TextRange(0, identifier.textLength))
            }
            TSFunctionType.GLOBAL_NS -> {
                TSFunctionReference(
                    this,
                    TextRange(
                        identifier.lastChild.startOffsetInParent,
                        identifier.lastChild.startOffsetInParent + identifier.lastChild.textLength
                    )
                )
            }
            TSFunctionType.METHOD -> {
                TSFunctionReference(
                    this,
                    TextRange(
                        identifier.lastChild.startOffsetInParent,
                        identifier.lastChild.startOffsetInParent + identifier.lastChild.textLength
                    )
                )
            }
        }
    }

    override fun getReferences(): Array<PsiReference> {
        val identifier = nameIdentifier ?: return arrayOf()
        return if (identifier.firstChild != identifier.lastChild) {
            arrayOf(
                reference!!,
                TSObjectReference(identifier.firstChild, TextRange(0, identifier.firstChild.textLength))
            )
        } else {
            arrayOf(reference!!)
        }
    }
}

