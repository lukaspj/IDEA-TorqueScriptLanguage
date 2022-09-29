package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfTypes
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl


class TSLocalVariableCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val functionParent = parameters.position.parentOfTypes(TSFunctionStatementElementImpl::class)
        if (functionParent != null) {
            PsiTreeUtil.findChildrenOfType(functionParent, TSVarExpressionElementImpl::class.java)
                .plus(functionParent.getParameters()
                    .filter { it.elementType == TSTypes.LOCALVAR || it.elementType == TSTypes.THISVAR })
        } else {
            // Assume file-scoped
            (parameters.originalFile as TSFile).getVariables()
        }?.forEach {
            val prefixless = it.text.substring(1)
            result.addElement(
                LookupElementBuilder.create(prefixless)
                    .withIcon(PlatformIcons.VARIABLE_ICON)
                    .withPresentableText(it.text)
                    .withCaseSensitivity(false)
            )
        }
    }
}
