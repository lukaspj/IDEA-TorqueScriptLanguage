package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTokenType


class TamlCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC, inTamlLiteral(), TamlPathCompletionContributor())
    }

    private fun inTamlLiteral(): ElementPattern<PsiElement> =
        psiElement(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)
}