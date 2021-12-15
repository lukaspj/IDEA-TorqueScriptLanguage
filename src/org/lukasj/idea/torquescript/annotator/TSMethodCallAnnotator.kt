package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.impl.TSFunctionCallExpressionElementImpl
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType

class TSMethodCallAnnotator : TSAnnotator() {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val engineApiService = element.project.getService(EngineApiService::class.java)
        if (element is TSFunctionStatementElementImpl) {
            val identifier = element.getFunctionIdentifier() ?: return

            val lastElement = identifier.lastChild
            createSuccessAnnotation(lastElement, holder, TSSyntaxHighlightingColors.FUNCTION_DECLARATION)

            if (lastElement.prevSibling?.prevSibling != null) {
                val namespace = identifier.firstChild

                if (ReferenceUtil.findObject(element.project, element.text).isNotEmpty()) {
                    createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.OBJECT_NAME)
                } else if (engineApiService.findClass(namespace.text) != null) {
                    createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.BUILTIN_CLASS_NAME)
                } else {
                    createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.CLASS_NAME)
                }
            }
        } else if (element is TSFunctionCallExpressionElementImpl) {
            when (element.functionType) {
                TSFunctionType.GLOBAL_NS -> {
                    val namespace = element.getExpression().firstChild
                    val functionName = element.getExpression().lastChild

                    if (ReferenceUtil.findObject(element.project, namespace.text).isNotEmpty()) {
                        createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.OBJECT_NAME)
                    } else if (engineApiService.findClass(namespace.text) != null) {
                        createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.BUILTIN_CLASS_NAME)
                    } else {
                        createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.CLASS_NAME)
                    }

                    if (engineApiService.findFunction(functionName.text) != null) {
                        createSuccessAnnotation(functionName, holder, TSSyntaxHighlightingColors.BUILTIN_FUNCTION_CALL)
                    } else {
                        createSuccessAnnotation(functionName, holder, TSSyntaxHighlightingColors.FUNCTION_CALL)
                    }
                }
                TSFunctionType.GLOBAL -> {
                    val functionName = element.getExpression().firstChild
                    if (engineApiService.findFunction(functionName.text) != null) {
                        createSuccessAnnotation(element.getExpression().firstChild, holder, TSSyntaxHighlightingColors.BUILTIN_FUNCTION_CALL)
                    } else {
                        createSuccessAnnotation(element.getExpression().firstChild, holder, TSSyntaxHighlightingColors.FUNCTION_CALL)
                    }
                }
                else -> return
            }
        }
    }
}