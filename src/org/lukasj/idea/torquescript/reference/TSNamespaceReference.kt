package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import icons.TSIcons
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.psi.impl.TSIdentExpressionElementImpl

class TSNamespaceReference(element: TSIdentExpressionElementImpl, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement.project

        val functions = ReferenceUtil.findFunction(project, element.lastChild.text)
            .filter { it.getFunctionType() == TSFunctionType.GLOBAL_NS }
            .filter { it.getNamespace() == element.firstChild.text }

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

    override fun handleElementRename(newElementName: String): PsiElement {
        val elem = element
        if (elem is PsiNameIdentifierOwner) {
            return elem.setName(newElementName)
        }
        return element
    }
}