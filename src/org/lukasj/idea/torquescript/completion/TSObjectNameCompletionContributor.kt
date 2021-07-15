package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.reference.ReferenceUtil

class TSObjectNameCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        ReferenceUtil.getObjects(parameters.position, parameters.position.project)
            .forEach { obj ->
                result.addElement(
                    LookupElementBuilder.create(obj)
                        .withPresentableText(obj.name!!)
                        .withCaseSensitivity(false)
                        .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
                )
            }
    }

}
