package org.lukasj.idea.torquescript.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTokenType
import com.intellij.refactoring.suggested.startOffset
import org.lukasj.idea.torquescript.ModuleFileType
import org.lukasj.idea.torquescript.TamlFileType
import org.lukasj.idea.torquescript.TamlLanguage
import org.lukasj.idea.torquescript.psi.TSLiteralExpression
import org.lukasj.idea.torquescript.reference.TSFileReference

class TamlPathAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element.elementType != XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
            return
        }
        if (element.language == TamlLanguage.INSTANCE) {
            return
        }

        element.parent.references
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