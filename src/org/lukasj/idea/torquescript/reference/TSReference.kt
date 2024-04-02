package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import icons.TSIcons

class TSReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {
    private val key: String = element.text.substring(textRange.startOffset, textRange.endOffset)

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement.project
        val functions = ReferenceUtil.getFunctions(project)

        return functions.map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return resolveResults.singleOrNull()?.element
    }

    override fun getVariants(): Array<Any> {
        val project = myElement.project
        val properties = ReferenceUtil.getFunctions(project)
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