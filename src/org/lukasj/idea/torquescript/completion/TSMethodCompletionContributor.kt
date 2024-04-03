package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.util.TSTypeLookupService

class TSMethodCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val namespace = parameters
            .position
            .prevSibling
            .prevSibling
            .text

        val project = parameters.originalFile.project
        val namespaces =
            if (namespace != null)
                project.getService(TSTypeLookupService::class.java).getNamespaceInheritanceList(namespace, project)
            else
                listOf()

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
            .filter { it.isCallback }
            .filter { ReferenceUtil.findFunction(project, "$namespace::${it.name}").isEmpty() }
            .map { method ->
                LookupElementBuilder.create(method, method.name)
                    .withIcon(PlatformIcons.FUNCTION_ICON)
                    .withCaseSensitivity(false)
                    .withTypeText(method.returnType)
                    .withPresentableText(method.toString())
                    .withTailText(method.arguments.joinToString(", ", "(", ")") { a -> a.toArgString() })
                    .withInsertHandler(TSMethodCallInsertHandler.INSTANCE)
            }
            .let { result.addAllElements(it) }
        result.stopHere()
    }
}
