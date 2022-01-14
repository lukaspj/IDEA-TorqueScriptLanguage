package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import icons.TSIcons
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.taml.TamlModuleService
import java.nio.file.Path

class TSPathCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val currentPath = parameters.position.text.let { it.substring(1, it.length - 1) }
            .let { it.substring(0, it.length - CompletionUtilCore.DUMMY_IDENTIFIER.length) }

        // Insert modules, IDEA has a prefix matcher so this shouldn't be noisy
        parameters.position.project.getService(TamlModuleService::class.java)
            .getModules()
            .forEach {
                result.addElement(
                    // We have to add the quote since we are inside a quote string
                    LookupElementBuilder.create("\"${it.moduleId}:")
                        .withIcon(TSIcons.MODULE)
                        .withPresentableText("${it.moduleId}")
                        .withCaseSensitivity(false)
                        .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
                )
            }

        when {
            currentPath.contains(":") ->
                currentPath.split(':')
                    .let { split ->
                        parameters.position.project.getService(TamlModuleService::class.java)
                            .getAssets(split[0])
                            .filter { it.assetName?.startsWith(split[1]) == true }
                            .forEach {
                                result.addElement(
                                    // We have to add the quote since we are inside a quote string
                                    LookupElementBuilder.create("\"${split[0]}:${it.assetName}")
                                        .withIcon(TSIcons.TAML)
                                        .withPresentableText("${split[0]}:${it.assetName}")
                                        .withCaseSensitivity(false)
                                        .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
                                )
                            }
                    }
            else ->
                if (currentPath.contains("/")) {
                    currentPath.substring(0, currentPath.lastIndexOf('/') + 1)
                } else {
                    ""
                }.let { currentDir ->
                    TSFileUtil.resolveScriptPath(parameters.originalFile, currentDir)?.children
                        ?.forEach {
                            // Handle an edge-case on Windows with beginning slash
                            if (currentDir.startsWith("/")) {
                                "/${Path.of(currentDir.substring(1), it.name)}"
                            } else {
                                Path.of(currentDir, it.name).toString()
                            }.let { sanitizedPath ->
                                result.addElement(
                                    // We have to add the quote since we are inside a quote string
                                    LookupElementBuilder.create("\"${sanitizedPath.replace('\\', '/')}")
                                        .withIcon(PlatformIcons.FILE_ICON)
                                        .withPresentableText(it.name)
                                        .withCaseSensitivity(false)
                                        .withTypeText(it.name)
                                        .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
                                )
                            }
                        }
                }
        }
    }

}
