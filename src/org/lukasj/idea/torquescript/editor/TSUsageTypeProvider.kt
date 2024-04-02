package org.lukasj.idea.torquescript.editor

import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProvider
import org.lukasj.idea.torquescript.psi.TSLiteralExpression
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.impl.TSFunctionIdentifierElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl

object TSUsageTypes {
    val FUNCTION_DEFINITION = UsageType {
        "Function definition"
    }
}

class TSUsageTypeProvider : UsageTypeProvider {
    override fun getUsageType(element: PsiElement): UsageType? {
        if (element is TSLiteralExpression) {
            return UsageType.LITERAL_USAGE
        }

        if (PsiTreeUtil.findFirstParent(element) {
                TokenSet.create(
                    TSTypes.NEW_INSTANCE_EXPRESSION,
                    TSTypes.DATABLOCK_STATEMENT,
                    TSTypes.SINGLETON_STATEMENT
                )
                    .contains(it.elementType)
            } != null)
            return UsageType.CLASS_NEW_OPERATOR

        val assignmentParent = PsiTreeUtil.findFirstParent(element) {
            TokenSet.create(TSTypes.ASSIGNMENT_EXPRESSION)
                .contains(it.elementType)
        }
        if (assignmentParent != null
            && assignmentParent.firstChild == element)
            return UsageType.WRITE

        val callParent = PsiTreeUtil.findFirstParent(element) {
            TokenSet.create(
                TSTypes.CALL_EXPRESSION,
                TSTypes.QUALIFIER_EXPRESSION
            )
                .contains(it.elementType)
        }
        if (callParent != null
            && callParent.firstChild == element)
            return UsageType.READ

        val funcIdentParent = PsiTreeUtil.findFirstParent(element) {
            TokenSet.create(TSTypes.FUNCTION_DECLARATION)
                .contains(it.elementType)
        }
        if (funcIdentParent != null
                && funcIdentParent is TSFunctionStatementElementImpl)
            return TSUsageTypes.FUNCTION_DEFINITION

        return null
    }
}