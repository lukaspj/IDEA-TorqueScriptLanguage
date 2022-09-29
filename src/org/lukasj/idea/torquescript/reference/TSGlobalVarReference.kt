package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import icons.TSIcons
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl

class TSGlobalVarReference(element: TSVarExpressionElementImpl, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {

    private val project = element.project

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
        ReferenceUtil.findGlobal(project, element.text)
            .map { PsiElementResolveResult(it) }
            .toTypedArray()

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<LookupElement> {
        val globals = ReferenceUtil.getGlobals(project)
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

    override fun handleElementRename(newElementName: String): PsiElement {
        val elem = element
        if (elem is PsiNameIdentifierOwner) {
            return elem.setName(newElementName)
        }
        return element
    }
}