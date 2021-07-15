package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.reference.ReferenceUtil

class TSMethodCallCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val caller = parameters
            .position
            .prevSibling
            .prevSibling

        val namespace =
            if (caller.node.elementType.equals(TSTypes.IDENT))
                caller.text
            else
                null

        val project = parameters.originalFile.project

        ReferenceUtil.getFunctions(parameters.position, project)
            .filter { it.getFunctionType() != TSFunctionType.GLOBAL }
            .forEach { function ->
                result.addElement(
                    LookupElementBuilder.create(function.name!!)
                        .withCaseSensitivity(false)
                        .withPresentableText("${function.getNamespace()}::${function.name}")
                        .withBoldness(namespace != null && function.getNamespace().equals(namespace, true))
                        .withTailText(function.getParameters().joinToString(prefix = "(", postfix = ")") { it.text })
                        .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
                )
            }
    }

}
