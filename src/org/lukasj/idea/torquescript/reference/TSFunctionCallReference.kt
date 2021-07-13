package org.lukasj.idea.torquescript.reference

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import org.lukasj.idea.torquescript.TSIcons
import org.lukasj.idea.torquescript.psi.impl.TSFunctionCallExpressionElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType

class TSFunctionCallReference(call: TSFunctionCallExpressionElementImpl, rangeInElement: TextRange?) :
    PsiReferenceBase<PsiElement>(call, rangeInElement),
    PsiPolyVariantReference {

    private val type = call.getFunctionType()
    private val name = call.name!!
    private val namespace =
        if (type != TSFunctionType.GLOBAL)
            call.nameIdentifier?.firstChild?.text
        else
            null
    private val project = call.project

    override fun multiResolve(incompleteCode: Boolean): Array<PsiElementResolveResult> =
        ReferenceUtil.findFunction(myElement, project, name)
            // You can access methods as namespace, but if it's a GLOBAL then the target function type must also be GLOBAL
            .filter { (it.getFunctionType() == TSFunctionType.GLOBAL) == (type == TSFunctionType.GLOBAL) }
            .filter { type == TSFunctionType.GLOBAL || it.getNamespace() == namespace  }
            .map { PsiElementResolveResult(it) }
            .toTypedArray()

    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }

    override fun getVariants(): Array<Any> =
        ReferenceUtil.getFunctions(myElement, project).filter {
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