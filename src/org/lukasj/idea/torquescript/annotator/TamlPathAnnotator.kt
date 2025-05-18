package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlElementType
import org.lukasj.idea.torquescript.TamlLanguage
import org.lukasj.idea.torquescript.reference.TSFileReference

class TamlPathAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.elementType != XmlElementType.XML_ATTRIBUTE_VALUE) {
            return
        }
        if (element.language != TamlLanguage.INSTANCE) {
            return
        }

        element.references
            .firstOrNull { it is TSFileReference }
            ?.let { reference ->
                reference.resolve()
                ?.let {
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(reference.absoluteRange)
                        .textAttributes(CodeInsightColors.INACTIVE_HYPERLINK_ATTRIBUTES)
                        .create()
                }
            }
    }
}