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

abstract class TSFunctionCallExpressionElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TSNamedElement {
    abstract fun getCallAccessor(): TSCallAccessor
    abstract fun getExpression(): TSExpression

    override fun getNameIdentifier(): PsiElement? =
        when {
            getFunctionType() == TSFunctionType.METHOD -> {
                null
            }
            getFunctionType() == TSFunctionType.GLOBAL -> {
                firstChild
            }
            getFunctionType() == TSFunctionType.GLOBAL_NS -> {
                getExpression()
            }
            else -> null
        }

    override fun getName(): String? =
        nameIdentifier?.text

    override fun setName(name: String): PsiElement {
        TODO("Not yet implemented")
    }

    fun getFunctionType(): TSFunctionType {
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
        val identifier = nameIdentifier
        return when(getFunctionType()) {
            TSFunctionType.GLOBAL -> {
                TSFunctionCallReference(this, TextRange(0, identifier!!.textLength))
            }
            TSFunctionType.GLOBAL_NS -> {
                TSFunctionCallReference(this, TextRange(0, identifier!!.textLength))
            }
            TSFunctionType.METHOD -> {
                TSFunctionCallReference(this, TextRange(0, identifier!!.textLength))
            }
            else -> null
        }
    }
}

