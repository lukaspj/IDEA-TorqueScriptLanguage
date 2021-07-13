package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.lukasj.idea.torquescript.TSIcons

class TSObjectReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement.project
        val objects = ReferenceUtil.findObject(myElement, project, element.text)

        return objects.map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> {
        val project = myElement.project
        val objects = ReferenceUtil.getObjects(myElement, project)
        return objects.filter {
            it.name != null && it.name!!.isNotEmpty()
        }.map { obj ->
            LookupElementBuilder
                .create(obj)
                .withIcon(TSIcons.FILE)
                .withTypeText(obj.containingFile.name)
                .withCaseSensitivity(false)
        }.toTypedArray()
    }
}