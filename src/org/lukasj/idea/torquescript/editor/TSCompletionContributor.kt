package org.lukasj.idea.torquescript.editor

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.elementType
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.psi.TSTypes

class TSCompletionContributor() : CompletionContributor() {
    init {
        extend(CompletionType.BASIC,
            PlatformPatterns.or(
                PlatformPatterns.psiElement(TSTypes.IDENT),
                PlatformPatterns.psiFile()
            ),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val parentType = parameters.position.parent.elementType
                    if (parentType == TSTypes.DATABLOCK_STATEMENT
                        || parentType == TSTypes.SINGLETON_STATEMENT
                        || parentType == TSTypes.PACKAGE_DECLARATION
                        || parentType == TSTypes.FUNCTION_IDENTIFIER
                    ) {
                        return
                    }


                    ReferenceUtil.getFunctions(parameters.position, parameters.position.project)
                        .filter {
                            it.name != null && it.name!!.isNotEmpty()
                        }.map { function ->
                            LookupElementBuilder
                                .create(function)
                                .withIcon(PlatformIcons.FUNCTION_ICON)
                                .withTailText(function.getParameters()
                                    .joinToString(", ", "(", ")") {
                                        it.text
                                    }
                                )
                                .withTypeText(function.containingFile.name)
                        }.forEach { result.addElement(it) }
                }
            }
        )
    }
}