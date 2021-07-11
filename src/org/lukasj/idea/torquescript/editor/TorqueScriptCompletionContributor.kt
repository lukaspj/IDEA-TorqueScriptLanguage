package org.lukasj.idea.torquescript.editor

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.elementType
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.TorqueScriptIcons
import org.lukasj.idea.torquescript.parser.ReferenceUtil
import org.lukasj.idea.torquescript.psi.TorqueScriptTypes

class TorqueScriptCompletionContributor() : CompletionContributor() {
    init {
        extend(CompletionType.BASIC,
            PlatformPatterns.or(
                PlatformPatterns.psiElement(TorqueScriptTypes.IDENT),
                PlatformPatterns.psiFile()
            ),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    println(parameters.position.parent.elementType!!.debugName)
                    if (parameters.position.parent.elementType == TorqueScriptTypes.TYPE_DECLARATION
                        || parameters.position.parent.elementType == TorqueScriptTypes.PACKAGE_STATEMENT
                        || parameters.position.parent.elementType == TorqueScriptTypes.FUNCTION_IDENTIFIER
                    ) {
                        return
                    }


                    ReferenceUtil.findFunctions(parameters.position.project)
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