package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.reference.ReferenceUtil

class TSGlobalVariableCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        ReferenceUtil.getGlobals(parameters.position, parameters.position.project)
            .forEach { global ->
                result.addElement(
                    LookupElementBuilder.create(global)
                        .withPresentableText(global.text)
                        .withCaseSensitivity(false)
                        .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
                )
            }
    }

}
