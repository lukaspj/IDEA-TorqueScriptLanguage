package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext

class TSKeywordCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) = KEYWORDS.forEach { result.addElement(LookupElementBuilder.create(it)) }

    companion object {
        val KEYWORDS = listOf(
            "datablock",
            "singleton",
            "function",
            "package",
            "namespace",
            "do",
            "while",
            "for",
            "foreach",
            "foreach$",
            "switch",
            "switch$",
            "if",
            "else",
            "case",
            "break",
            "continue",
            "return",
            "in",
            "new",
            "assert",
            "true",
            "false",
        )
    }
}
