package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors
import org.lukasj.idea.torquescript.psi.TSDatablockStatement
import org.lukasj.idea.torquescript.psi.TSNewInstanceExpression
import org.lukasj.idea.torquescript.psi.TSSingletonStatement
import org.lukasj.idea.torquescript.psi.TSTypes

class TSClassNameAnnotator : TSAnnotator() {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val classElement = when (element) {
            is TSDatablockStatement -> {
                element.firstChild.nextSibling.nextSibling
            }
            is TSSingletonStatement -> {
                element.firstChild.nextSibling.nextSibling
            }
            is TSNewInstanceExpression -> {
                element.firstChild.nextSibling.nextSibling
            }
            else -> return
        }

        if (classElement.elementType == TSTypes.IDENT) {
            createSuccessAnnotation(classElement, holder, TSSyntaxHighlightingColors.CLASS_NAME)
        } else {
            createWarnAnnotation(classElement, holder, "TSClassNameAnnotator: Unhandled elementType: ${classElement.text} - ${classElement.elementType}")
        }
    }
}