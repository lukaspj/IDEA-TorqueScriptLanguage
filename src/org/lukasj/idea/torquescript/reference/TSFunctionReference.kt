package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import icons.TSIcons
import org.lukasj.idea.torquescript.util.TSTypeLookupService

class TSFunctionReference(
    element: PsiNamedElement,
    textRange: TextRange,
    val namespace: String? = null,
    val functionName: String? = null
) : PsiReferenceBase<PsiNamedElement>(element, textRange), PsiPolyVariantReference {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement.project
        val functions = if (namespace != null) {
            project.getService(TSTypeLookupService::class.java)
                .getNamespaceInheritanceList(namespace, project)
                .flatMap { ReferenceUtil.findFunction(project, "${it}::${functionName ?: element.name}") }
        } else {
            ReferenceUtil.findFunction(project, functionName ?: element.name!!)
        }

        return functions
            .map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return resolveResults.firstOrNull()?.element
    }

    override fun getVariants(): Array<Any> {
        val project = myElement.project
        val functions = ReferenceUtil.getFunctions(project)
        return functions.filter {
            it.name != null && it.name!!.isNotEmpty()
        }.map { function ->
            LookupElementBuilder
                .create(function)
                .withIcon(TSIcons.FILE)
                .withTypeText(function.containingFile.name)
                .withTailText(function.getParameters().joinToString(", ", "(", ")") { it.text })
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