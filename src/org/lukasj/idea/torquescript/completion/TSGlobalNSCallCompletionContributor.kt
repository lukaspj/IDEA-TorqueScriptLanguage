package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.prevLeaf
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.reference.ReferenceUtil

class TSGlobalNSCallCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val namespace = parameters
            .position
            .prevLeaf()
            ?.prevLeaf()
            ?.text ?: return

        val project = parameters.originalFile.project

        ReferenceUtil.getFunctions(project)
            .filter { it.getFunctionType() != TSFunctionType.GLOBAL }
            .filter { it.getNamespace().equals(namespace, true) }
            .map { function ->
                LookupElementBuilder.create(function.name!!)
                    .withIcon(PlatformIcons.FUNCTION_ICON)
                    .withCaseSensitivity(false)
                    .withTypeText(function.containingFile.name)
                    .withPresentableText("${function.getNamespace()}::${function.name}")
                    .withTailText(function.getParameters().joinToString (prefix = "(", postfix = ")") { it.text })
                    .withInsertHandler(TSMethodCallInsertHandler.INSTANCE)
            }
            .plus(
                project.getService(EngineApiService::class.java)
                    .getStaticFunctions()
                    .filter { it.scopeList.lastOrNull().equals(namespace, true) }
                    .map {
                        LookupElementBuilder.create("${namespace}::${it.name}")
                            .withPresentableText(it.toString())
                            .withIcon(PlatformIcons.FUNCTION_ICON)
                            .withCaseSensitivity(false)
                            .withTypeText(it.returnType)
                            .withTailText(it.arguments.joinToString(", ", "(", ")") { a -> a.toArgString() })
                            .withInsertHandler(TSMethodCallInsertHandler.INSTANCE)
                    }
            )
            .forEach { result.addElement(it) }
    }

}
