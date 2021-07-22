package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.psi.PsiElement
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors
import org.lukasj.idea.torquescript.psi.TSFunctionIdentifier
import org.lukasj.idea.torquescript.psi.TSIdentExpression
import org.lukasj.idea.torquescript.reference.TSObjectReference

class TSObjectAnnotator : TSAnnotator() {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is TSIdentExpression) {
            val ref = element.reference
            if (ref != null
                && ref is TSObjectReference
                && ref.multiResolve(false).isNotEmpty()
            ) {
                createSuccessAnnotation(element, holder, TSSyntaxHighlightingColors.OBJECT_NAME)
            }
        } else if (element is TSFunctionIdentifier) {
            val ref = element.reference
            if (ref != null
                && ref is TSObjectReference
                && ref.multiResolve(false).isNotEmpty()
            ) {
                createSuccessAnnotation(element, holder, TSSyntaxHighlightingColors.OBJECT_NAME)
            }
        }
    }
}