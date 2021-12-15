package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.PlatformIcons
import com.intellij.util.ProcessingContext
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.TSDatablockStatement
import org.lukasj.idea.torquescript.psi.TSNewInstanceExpression
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.psi.TSSingletonStatement

class TSPropertiesCompletionContributor : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val project = parameters.originalFile.project

        val contextElement = PsiTreeUtil.getParentOfType(parameters.position, TSObjectDeclaration::class.java)
            ?: return

        project.getService(EngineApiService::class.java)
            .findClass(contextElement.getTypeName())
            ?.let { engineClass ->
                engineClass.properties
                    .forEach { property ->
                        if (property.indexedSize == 1) {
                            result.addElement(
                                LookupElementBuilder.create(property)
                                    .withIcon(PlatformIcons.FIELD_ICON)
                                    .withCaseSensitivity(false)
                                    .withTypeText(property.typeName)
                                    .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
                            )
                        } else {
                            for (idx in 1..property.indexedSize) {
                                result.addElement(
                                    LookupElementBuilder.create("${property}[${idx}]")
                                        .withIcon(PlatformIcons.FIELD_ICON)
                                        .withCaseSensitivity(false)
                                        .withTypeText(property.typeName)
                                        .withInsertHandler(TSCaseCorrectingInsertHandler.INSTANCE)
                                )
                            }
                        }
                    }
            }

        result.stopHere()
    }

}
