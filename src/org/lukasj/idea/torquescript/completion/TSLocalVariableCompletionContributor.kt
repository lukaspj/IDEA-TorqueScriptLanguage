package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.psi.TSTypes


class TSLocalVariableCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        var current = parameters.position.prevSibling
        while (current != null) {
            if (current.node.elementType == TSTypes.FUNCTION) break
            if (current.node.elementType == TSTypes.LOCALVAR) {
                val prefixless = current.text.substring(1)
                result.addElement(
                    LookupElementBuilder.create(prefixless)
                        .withPresentableText(current.text)
                        .withCaseSensitivity(false)
                )
            }
            current = current.prevSibling
        }
    }
}
