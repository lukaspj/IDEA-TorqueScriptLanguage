package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.TSDatablockStatement
import org.lukasj.idea.torquescript.psi.TSNewInstanceExpression
import org.lukasj.idea.torquescript.psi.TSSingletonStatement
import org.lukasj.idea.torquescript.psi.TSTypes

class TSClassNameAnnotator : TSAnnotator() {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val engineApiService = element.project.getService(EngineApiService::class.java)

        val classElement = when (element) {
            is TSDatablockStatement -> {
                element.getObjectTypeName()
            }
            is TSSingletonStatement -> {
                element.getObjectTypeName()
            }
            is TSNewInstanceExpression -> {
                element.getObjectTypeName()
            }
            else -> return
        }

        if (classElement.elementType == TSTypes.OBJECT_TYPE_NAME) {
            if (engineApiService.findClass(classElement.text) != null) {
                createSuccessAnnotation(classElement, holder, TSSyntaxHighlightingColors.BUILTIN_CLASS_NAME)
            } else {
                createSuccessAnnotation(classElement, holder, TSSyntaxHighlightingColors.CLASS_NAME)
            }
        } else {
            createWarnAnnotation(classElement, holder, "TSClassNameAnnotator: Unhandled elementType: ${classElement.text} - ${classElement.elementType}")
        }
    }
}