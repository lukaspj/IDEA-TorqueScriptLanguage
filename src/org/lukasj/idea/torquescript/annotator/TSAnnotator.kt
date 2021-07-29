package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement


open class TSAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder): Unit =
        annotators.get().forEach { it.annotate(element, holder) }


    open fun createSuccessAnnotation(
        element: PsiElement,
        holder: AnnotationHolder,
        key: TextAttributesKey
    ) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
            .range(element)
            .create()
        holder
            .newAnnotation(HighlightSeverity.INFORMATION, "")
            .textAttributes(key)
            .range(element)
            .create()
    }


    open fun createSuccessAnnotationWithTooltip(
        element: PsiElement,
        holder: AnnotationHolder,
        key: TextAttributesKey,
        tooltip: String
    ) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
            .range(element)
            .create()
        holder
            .newAnnotation(HighlightSeverity.INFORMATION, "")
            .textAttributes(key)
            .tooltip(tooltip)
            .range(element)
            .create()
    }

    open fun createWarnAnnotation(
        element: PsiElement,
        holder: AnnotationHolder,
        message: String
    ) = holder.newAnnotation(HighlightSeverity.WARNING, message).create()

    companion object {
        val annotators: ThreadLocal<Array<TSAnnotator>> = ThreadLocal.withInitial {
            arrayOf(
                TSMethodCallAnnotator(),
                TSObjectAnnotator(),
                TSClassNameAnnotator(),
                TSPathAnnotator(),
            )
        }
    }
}