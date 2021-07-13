package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.lukasj.idea.torquescript.TSIcons
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl

class TSGlobalVarReference(element: TSVarExpressionElementImpl, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {

    private val project = element.project
    private val name = element.name

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
        ReferenceUtil.findGlobal(myElement, project, element.text)
            .map { PsiElementResolveResult(it) }
            .toTypedArray()

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> {
        val globals = ReferenceUtil.getGlobals(myElement, project)
        return globals.filter {
            it.name != null && it.name!!.isNotEmpty()
        }.map { global ->
            LookupElementBuilder
                .create(global)
                .withIcon(TSIcons.FILE)
                .withTypeText(global.containingFile.name)
                .withCaseSensitivity(false)
        }.toTypedArray()
    }
}