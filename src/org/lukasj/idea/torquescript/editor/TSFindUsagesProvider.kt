package org.lukasj.idea.torquescript.editor

import com.intellij.lang.cacheBuilder.DefaultWordsScanner
import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.lukasj.idea.torquescript.lexer.TSLexerAdapter
import org.lukasj.idea.torquescript.psi.*
import org.lukasj.idea.torquescript.psi.impl.*

class TSFindUsagesProvider : FindUsagesProvider {
    override fun getWordsScanner() =
        DefaultWordsScanner(
            TSLexerAdapter(),
            TokenSet.create(TSTypes.IDENT, TSTypes.THISVAR, TSTypes.LOCALVAR, TSTypes.GLOBALVAR),
            TokenSet.create(TSTypes.BLOCK_COMMENT, TSTypes.LINE_COMMENT, TSTypes.DOC_COMMENT),
            TokenSet.create(TSTypes.QUOTED_STRING, TSTypes.TAGGED_STRING, TSTypes.FLOAT, TSTypes.HEXDIGIT, TSTypes.INTEGER)
        )

    override fun canFindUsagesFor(psiElement: PsiElement) =
        psiElement is TSNamedElement

    override fun getHelpId(psiElement: PsiElement): String? = null

    override fun getType(element: PsiElement): String =
        when (element) {
            is TSFunctionStatementElementImpl -> "function"
            is TSFunctionDeclarationImpl -> "function"
            is TSFunctionCallExpressionElementImpl -> "function"
            is TSFunctionIdentifierElementImpl -> "function"
            is TSVarExpressionElementImpl -> "variable"
            is TSObjectDeclaration -> "object"
            is TSNewInstanceExpression -> "object"
            else -> ""
        }

    override fun getDescriptiveName(element: PsiElement): String =
        when (element) {
            is TSFunctionStatementElementImpl -> element.name ?: ""
            is TSFunctionDeclarationImpl -> element.name ?: ""
            is TSFunctionCallExpressionElementImpl -> element.name ?: ""
            is TSFunctionIdentifierElementImpl -> element.name ?: ""
            is TSVarExpressionElementImpl -> element.name ?: ""
            is TSObjectDeclaration -> element.name ?: ""
            else -> ""
        }

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        element.text
}