package org.lukasj.idea.torquescript.reference

import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.util.ProcessingContext

class TamlReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(XmlAttributeValue::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<PsiReference> {
                    val attributeValue = element as XmlAttributeValue

                    return when {
                        attributeValue.value.startsWith("@assetFile=") ->
                            arrayOf(
                                TSFileReference(
                                    element,
                                    TextRange("@assetFile=".length + 1, attributeValue.textLength - 1),
                                    true
                                )
                            )
                        attributeValue.value.startsWith("@asset=") ->
                            arrayOf(
                                TSFileReference(
                                    element,
                                    TextRange("@asset=".length + 1, attributeValue.textLength - 1),
                                    true
                                )
                            )

                        attributeValue.value.contains("/") || attributeValue.value.contains(":") ->
                            arrayOf(TSFileReference(element, TextRange(1, attributeValue.textLength - 1), true))
                        else -> arrayOf()
                    }
                }
            })
    }
}