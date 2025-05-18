package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.startOffset
import org.lukasj.idea.torquescript.psi.TSLiteralExpression
import org.lukasj.idea.torquescript.reference.TSFileReference

class TSPathAnnotator : TSAnnotator() {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is TSLiteralExpression) {
            return
        }

        if (element.reference is TSFileReference && element.reference?.resolve() != null) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(element.startOffset + 1, element.startOffset + element.textLength - 1))
                .textAttributes(CodeInsightColors.INACTIVE_HYPERLINK_ATTRIBUTES)
                .create()
        }
    }
}