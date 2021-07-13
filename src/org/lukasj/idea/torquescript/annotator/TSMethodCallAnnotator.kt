package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors
import org.lukasj.idea.torquescript.psi.impl.TSFunctionCallExpressionElementImpl
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType

class TSMethodCallAnnotator : TSAnnotator() {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is TSFunctionStatementElementImpl) {
            val identifier = element.getFunctionIdentifier()

            val lastElement = identifier!!.lastChild
            createSuccessAnnotation(lastElement, holder, TSSyntaxHighlightingColors.FUNCTION_DECLARATION)

            if (lastElement.prevSibling?.prevSibling != null) {
                val namespace = identifier.firstChild

                if (ReferenceUtil.findObject(element, element.project, element.text).isNotEmpty()) {
                    createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.OBJECT_NAME)
                } else {
                    createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.CLASS_NAME)
                }
            }
        } else if (element is TSFunctionCallExpressionElementImpl) {
            when (element.getFunctionType()) {
                TSFunctionType.GLOBAL_NS -> {
                    val namespace = element.getExpression().firstChild
                    val functionName = element.getExpression().lastChild

                    if (ReferenceUtil.findObject(element, element.project, namespace.text).isNotEmpty()) {
                        createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.OBJECT_NAME)
                    } else {
                        createSuccessAnnotation(namespace, holder, TSSyntaxHighlightingColors.CLASS_NAME)
                    }

                    createSuccessAnnotation(functionName, holder, TSSyntaxHighlightingColors.FUNCTION_CALL)
                }
                TSFunctionType.GLOBAL -> {
                    createSuccessAnnotation(element.getExpression().firstChild, holder, TSSyntaxHighlightingColors.FUNCTION_CALL)
                }
                else -> return
            }
        }
    }
}