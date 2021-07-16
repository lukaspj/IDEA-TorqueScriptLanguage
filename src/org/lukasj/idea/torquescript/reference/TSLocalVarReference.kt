package org.lukasj.idea.torquescript.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfTypes
import org.lukasj.idea.torquescript.psi.*
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl

class TSLocalVarReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val functionParent = element.parentOfTypes(TSFunctionStatementElementImpl::class)
        if (functionParent != null) {
            return PsiTreeUtil.findChildrenOfType(functionParent, TSVarExpressionElementImpl::class.java)
                .plus(functionParent.getParameters()
                    .filter { it.elementType == TSTypes.LOCALVAR || it.elementType == TSTypes.THISVAR })
                .filter { it.text.equals(element.text, true) }
                .map { PsiElementResolveResult(it) }
                .toTypedArray()
        } else {
            // Assume file-scoped
            val file = element.containingFile as TSFile
            return file.getVariables()
                ?.filter { it.text.equals(element.text, true) }
                ?.map { PsiElementResolveResult(it) }
                ?.toTypedArray()
                ?: arrayOf()
        }
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }
}