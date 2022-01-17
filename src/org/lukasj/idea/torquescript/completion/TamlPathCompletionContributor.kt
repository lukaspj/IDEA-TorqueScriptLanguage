package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import icons.TSIcons
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.taml.TamlModuleService
import java.nio.file.Path

class TamlPathCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val currentPath = parameters.position.text
            .let { it.substring(0, it.length - CompletionUtilCore.DUMMY_IDENTIFIER.length) }

        // Insert modules, IDEA has a prefix matcher so this shouldn't be noisy
        parameters.position.project.getService(TamlModuleService::class.java)
            .getModules()
            .forEach {
                result.addElement(
                    // We have to add the quote since we are inside a quote string
                    LookupElementBuilder.create("${it.moduleId}:")
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
                                    LookupElementBuilder.create("${split[0]}:${it.assetName}")
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
                    TSFileUtil.resolveScriptPath(parameters.originalFile, currentDir)?.let { path ->
                        VfsUtil.findFile(path, true)?.children
                            ?.forEach {
                                result.addElement(
                                    // We have to add the quote since we are inside a quote string
                                    LookupElementBuilder.create(
                                        Path.of(currentDir, it.name).toString().replace('\\', '/')
                                    )
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
