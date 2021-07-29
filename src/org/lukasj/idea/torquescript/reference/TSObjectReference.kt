package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import icons.TSIcons

class TSObjectReference(element: PsiElement, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement.project
        val objects = ReferenceUtil.findObject(project, rangeInElement.substring(element.text))

        return objects.map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return resolveResults.firstOrNull()?.element
    }

    override fun getVariants(): Array<Any> {
        val project = myElement.project
        val objects = ReferenceUtil.getObjects(project)
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

    override fun handleElementRename(newElementName: String): PsiElement {
        val elem = element
        if (elem is PsiNamedElement) {
            return elem.setName(newElementName)
        }
        return element
    }
}