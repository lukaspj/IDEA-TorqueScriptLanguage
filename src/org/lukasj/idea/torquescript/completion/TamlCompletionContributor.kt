package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTokenType
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.TamlLanguage
import org.lukasj.idea.torquescript.psi.TSFieldAssignment
import org.lukasj.idea.torquescript.psi.TSLiteralExpression
import org.lukasj.idea.torquescript.psi.TSProperty
import org.lukasj.idea.torquescript.psi.TSTypes


class TamlCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC, inTamlLiteral(), TamlPathCompletionContributor())
    }

    private fun inTamlLiteral(): ElementPattern<PsiElement> =
        psiElement(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)
}