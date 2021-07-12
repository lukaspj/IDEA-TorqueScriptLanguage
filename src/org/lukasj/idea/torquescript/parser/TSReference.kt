package org.lukasj.idea.torquescript.parser

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.lukasj.idea.torquescript.TSIcons

class TSReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement.project
        val functions = ReferenceUtil.findFunctions(project)

        return functions.map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> {
        val project = myElement.project
        val properties = ReferenceUtil.findFunctions(project)
        return properties.filter {
            it.name != null && it.name!!.isNotEmpty()
        }.map { property ->
            LookupElementBuilder
                .create(property)
                .withIcon(TSIcons.FILE)
                .withTypeText(property.containingFile.name)
        }.toTypedArray()
    }
}