package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.psi.TSTypes


class TSCompletionContributor : CompletionContributor() {
    init {
        extend(CompletionType.BASIC, inMethod(), TSMethodCompletionContributor())
        extend(CompletionType.BASIC, inDatablock(), TSClassCompletionContributor())
        extend(CompletionType.BASIC, inSingleton(), TSClassCompletionContributor())
        extend(CompletionType.BASIC, inNew(), TSClassCompletionContributor())
        extend(CompletionType.BASIC, isKeywordable(), TSKeywordCompletionContributor())
        extend(CompletionType.BASIC, inGlobalCall(), TSGlobalCallCompletionContributor())
        extend(CompletionType.BASIC, inGlobalNSCall(), TSGlobalNSCallCompletionContributor())
        extend(CompletionType.BASIC, inMethodCall(), TSMethodCallCompletionContributor())
        extend(CompletionType.BASIC, inLocalVariable(), TSLocalVariableCompletionContributor())
        extend(CompletionType.BASIC, inGlobalVariable(), TSGlobalVariableCompletionContributor())
        extend(CompletionType.BASIC, inObjectName(), TSObjectNameCompletionContributor())
    }

    private fun isKeywordable(): ElementPattern<PsiElement> =
        psiElement(TSTypes.IDENT).withLanguage(TSLanguage.INSTANCE)
            .andNot(psiElement().afterLeaf(psiElement(TSTypes.DOT)))
            .andNot(psiElement().afterLeaf(psiElement(TSTypes.COLON_COLON)))

    private fun inGlobalCall(): ElementPattern<PsiElement> =
        isKeywordable()

    private fun inGlobalNSCall(): ElementPattern<PsiElement> =
        psiElement(TSTypes.IDENT)
            .withLanguage(TSLanguage.INSTANCE)
            .afterLeaf(psiElement(TSTypes.COLON_COLON))
            .andNot(
                psiElement()
                    .afterLeafSkipping(
                        psiElement()
                            .withElementType(
                                TokenSet.create(
                                    TokenType.WHITE_SPACE,
                                    TSTypes.IDENT,
                                    TSTypes.COLON_COLON
                                )
                            ),
                        psiElement(TSTypes.FUNCTION)
                    )
            )

    private fun inMethodCall(): ElementPattern<PsiElement> =
        psiElement(TSTypes.IDENT)
            .withLanguage(TSLanguage.INSTANCE)
            .afterLeaf(psiElement(TSTypes.DOT))

    private fun inLocalVariable(): ElementPattern<PsiElement> =
        StandardPatterns.or(
            psiElement(TSTypes.IDENT)
                .andNot(psiElement().afterLeaf(psiElement(TSTypes.DOT)))
                .andNot(psiElement().afterLeaf(psiElement(TSTypes.COLON_COLON))),
            psiElement(TSTypes.LOCALVAR)
        )

    private fun inGlobalVariable(): ElementPattern<PsiElement> =
        StandardPatterns.or(
            psiElement(TSTypes.IDENT)
                .andNot(psiElement().afterLeaf(psiElement(TSTypes.DOT)))
                .andNot(psiElement().afterLeaf(psiElement(TSTypes.COLON_COLON))),
            psiElement(TSTypes.GLOBALVAR)
        )

    private fun inObjectName(): ElementPattern<PsiElement> =
        isKeywordable()

    private fun inMethod(): ElementPattern<PsiElement> =
        psiElement(TSTypes.IDENT)
            .afterLeaf(psiElement(TSTypes.COLON_COLON))
            .afterLeafSkipping(
                psiElement()
                    .withElementType(TokenSet.create(TokenType.WHITE_SPACE, TSTypes.IDENT, TSTypes.COLON_COLON)),
                psiElement(TSTypes.FUNCTION)
            )

    private fun inDatablock(): ElementPattern<PsiElement> =
        psiElement(TSTypes.IDENT)
            .and(
                psiElement().afterLeafSkipping(
                    psiElement(TokenType.WHITE_SPACE),
                    psiElement(TSTypes.DATABLOCK)
                )
            )

    private fun inSingleton(): ElementPattern<PsiElement> =
        psiElement(TSTypes.IDENT)
            .and(
                psiElement().afterLeafSkipping(
                    psiElement(TokenType.WHITE_SPACE),
                    psiElement(TSTypes.SINGLETON)
                )
            )

    private fun inNew(): ElementPattern<PsiElement> =
        psiElement(TSTypes.IDENT)
            .and(
                psiElement().afterLeafSkipping(
                    psiElement(TokenType.WHITE_SPACE),
                    psiElement(TSTypes.NEW)
                )
            )
}