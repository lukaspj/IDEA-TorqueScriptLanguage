package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.reference.ReferenceUtil

class TSObjectNameCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) = ReferenceUtil.getObjects(parameters.position.project)
        .filter { it.name != null }
        .map { obj ->
            LookupElementBuilder.create(obj)
                .withIcon(PlatformIcons.CLASS_INITIALIZER)
                .withPresentableText(obj.name!!)
                .withCaseSensitivity(false)
                .withTypeText(obj.containingFile.name)
                .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
        }
        .forEach { result.addElement(it) }

}
