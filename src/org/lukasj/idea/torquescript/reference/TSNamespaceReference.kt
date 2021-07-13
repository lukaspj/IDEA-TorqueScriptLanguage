package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.lukasj.idea.torquescript.TSIcons
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.psi.impl.TSIdentExpressionElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSIdentExpressionImpl

class TSNamespaceReference(element: TSIdentExpressionElementImpl, textRange: TextRange) : PsiReferenceBase<PsiElement>(element, textRange),
    PsiPolyVariantReference {
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = myElement.project

        val functions = ReferenceUtil.findFunction(myElement, project, element.lastChild.text)
            .filter { it.getFunctionType() == TSFunctionType.GLOBAL_NS }
            .filter { it.getNamespace() == element.firstChild.text }

        return functions.map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> {
        val project = myElement.project
        val properties = ReferenceUtil.getFunctions(myElement, project)
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