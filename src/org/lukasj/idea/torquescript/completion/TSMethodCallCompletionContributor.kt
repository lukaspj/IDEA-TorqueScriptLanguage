package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.util.TSTypeLookupService

class TSMethodCallCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val caller = parameters
            .position
            .prevSibling
            ?.prevSibling ?: return

        val namespace = ReferenceUtil.tryResolveType(caller)

        val project = parameters.originalFile.project
        val namespaces =
            if (namespace != null)
                project.getService(TSTypeLookupService::class.java).getNamespaceInheritanceList(namespace, project)
            else
                listOf()

        ReferenceUtil.getFunctions(project)
            .filter { it.getFunctionType() != TSFunctionType.GLOBAL }
            .filter { func -> namespaces.isEmpty() || namespaces.any { func.getNamespace() == it } }
            .map { function ->
                LookupElementBuilder.create(function.name!!)
                    .withIcon(PlatformIcons.METHOD_ICON)
                    .withCaseSensitivity(false)
                    .withPresentableText("${function.getNamespace()}::${function.name}")
                    .withBoldness(namespace != null && function.getNamespace().equals(namespace, true))
                    .withTailText(function.getParameters().joinToString(prefix = "(", postfix = ")") { it.text })
                    .withTypeText(function.containingFile.name)
                    .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
            }
            .plus(
                namespaces
                    .flatMap { project.getService(EngineApiService::class.java).getMethods(it) }
                    .plus(
                        if (namespaces.isEmpty()) {
                            project.getService(EngineApiService::class.java)
                                .getFunctions()
                        } else {
                            listOf()
                        }
                    )
                    .filter { !it.isStatic }
                    .map { method ->
                        LookupElementBuilder.create(method, method.name)
                            .withIcon(PlatformIcons.FUNCTION_ICON)
                            .withCaseSensitivity(false)
                            .withTypeText(method.returnType)
                            .withPresentableText(method.toString())
                            .withTailText(method.arguments.joinToString(", ", "(", ")") { a -> a.toArgString() })
                            .withInsertHandler(TSMethodCallInsertHandler.INSTANCE)
                    }
            )
            .forEach { result.addElement(it) }
    }
}
