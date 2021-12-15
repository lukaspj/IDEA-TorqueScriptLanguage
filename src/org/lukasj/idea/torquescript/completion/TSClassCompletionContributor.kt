package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.engine.EngineApiService

class TSClassCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val project = parameters.originalFile.project

        project.getService(EngineApiService::class.java)
            .getClasses()
            .map {
                LookupElementBuilder.create(it)
                    .withIcon(PlatformIcons.CLASS_ICON)
                    .withCaseSensitivity(false)
                    .withTypeText(it.superType)
                    .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
            }
            .forEach {
                result.addElement(it)
            }

        result.stopHere()
    }

}
