package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.impl.TSAccessorChainImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionCallExpressionElementImpl
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.psi.impl.TSPropertyElementImpl
import org.lukasj.idea.torquescript.util.TSTypeLookupService

class TSMethodCallAnnotator : TSAnnotator() {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val engineApiService = element.project.getService(EngineApiService::class.java)
        val typeLookupService = element.project.getService(TSTypeLookupService::class.java)
        if (element is TSFunctionStatementElementImpl) {
            val identifier = element.getFunctionIdentifier() ?: return

            val lastElement = identifier.lastChild
            createSuccessAnnotation(lastElement, holder, TSSyntaxHighlightingColors.FUNCTION_DECLARATION)

            if (lastElement.prevSibling?.prevSibling != null) {
                val namespace = identifier.firstChild

                if (typeLookupService.findObject(element.project, element.text).isNotEmpty()) {
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

                    if (typeLookupService.findObject(element.project, namespace.text).isNotEmpty()) {
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
        } else if (element is TSPropertyElementImpl) {
            if (element.nextSibling is TSAccessorChainImpl) {
                // Only handle simple cases, the call chain is not an array of operators, rather it is like a deeply
                // nested list of operators. So, the case where the parent has a prevSibling is only at the beginning of
                // the chain.
                // This is the only case we can hope to handle right now. We could handle shallow chains by inferring
                // types of fields, theoretically, but that seems like goldplating at this point.
                if (element.parent.prevSibling == null) {
                    return
                }

                val namespace = ReferenceUtil.tryResolveType(element.parent.prevSibling)
                val functionName = element

                if (namespace != null) {
                    if (
                        typeLookupService.getNamespaceInheritanceList(namespace, element.project)
                            .any {
                                engineApiService.findMethod(it, functionName.text) != null
                            }
                    ) {
                        createSuccessAnnotation(functionName, holder, TSSyntaxHighlightingColors.BUILTIN_FUNCTION_CALL)
                    } else {
                        createSuccessAnnotation(functionName, holder, TSSyntaxHighlightingColors.FUNCTION_CALL)
                    }
                } else {
                    createSuccessAnnotation(functionName, holder, TSSyntaxHighlightingColors.FUNCTION_CALL)
                }
            }
        }
    }
}