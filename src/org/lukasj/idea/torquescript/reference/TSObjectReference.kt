package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import icons.TSIcons
import org.lukasj.idea.torquescript.util.TSTypeLookupService

class TSObjectReference(element: PsiNamedElement, textRange: TextRange) : PsiReferenceBase<PsiNamedElement>(element, textRange),
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
        val objects = project.getService(TSTypeLookupService::class.java).getObjects(project)
        return objects.map { obj ->
            LookupElementBuilder
                .create(obj)
                .withIcon(TSIcons.FILE)
                .withTypeText(obj.containingFile.name)
                .withCaseSensitivity(false)
        }.toTypedArray()
    }

    override fun handleElementRename(newElementName: String): PsiElement = element.setName(newElementName)
}