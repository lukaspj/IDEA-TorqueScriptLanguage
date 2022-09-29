package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfTypes
import com.intellij.util.PlatformIcons
import org.lukasj.idea.torquescript.psi.*
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl

class TSLocalVarReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {

    override fun multiResolve(incompleteCode: Boolean) =
        ReferenceUtil.findLocalVariablesForContext(element)
            .let { localVariableExpression ->
                localVariableExpression
                    .filter { it.parent is TSAssignmentExpression }
                    .filterNot { it.textRange.contains(element.textRange) }
                    .filter { it.text.equals(element.text, true) }
                    .plus(
                        localVariableExpression
                            .filter { it.parent.elementType == TSTypes.PARAMS }
                    )
            }
            .map { PsiElementResolveResult(it) }
            .toTypedArray()

    override fun getVariants(): Array<LookupElement> {
        val functionParent = element.parentOfTypes(TSFunctionStatementElementImpl::class)
        if (functionParent != null) {
            return PsiTreeUtil.findChildrenOfType(functionParent, TSVarExpressionElementImpl::class.java)
                .plus(functionParent.getParameters()
                    .filter { it.elementType == TSTypes.LOCALVAR || it.elementType == TSTypes.THISVAR })
                .map { global ->
                    LookupElementBuilder
                        .create(global)
                        .withIcon(PlatformIcons.VARIABLE_ICON)
                        .withTypeText(global.containingFile.name)
                        .withCaseSensitivity(false)
                }.toTypedArray()
        } else {
            // Assume file-scoped
            val file = element.containingFile as TSFile
            return (file).getVariables()
                ?.filter { it.text.equals(element.text, true) }
                ?.map { local ->
                    LookupElementBuilder
                        .create(local)
                        .withIcon(PlatformIcons.VARIABLE_ICON)
                        .withTypeText(local.containingFile.name)
                        .withCaseSensitivity(false)
                }
                ?.toTypedArray()
                ?: arrayOf()
        }
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun handleElementRename(newElementName: String): PsiElement {
        val elem = element
        if (elem is PsiNameIdentifierOwner) {
            return elem.setName(newElementName)
        }
        return element
    }
}