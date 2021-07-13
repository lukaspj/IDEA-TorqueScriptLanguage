package org.lukasj.idea.torquescript.editor

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.lukasj.idea.torquescript.parser.TSParserDefinition
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.BAD_CHARACTER
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.BLOCK_COMMENT
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.BRACES
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.BRACKETS
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.COLON
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.COMMA
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.DOC_COMMENT
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.DOT
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.GLOBALVAR
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.IDENTIFIER
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.KEYWORD
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.LINE_COMMENT
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.LOCALVAR
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.NUMBER
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.OPERATOR
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.PARENTHESES
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.SEMICOLON
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.STRING
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.TAG
import org.lukasj.idea.torquescript.editor.TSSyntaxHighlightingColors.THISVAR
import org.lukasj.idea.torquescript.lexer.TSLexerAdapter
import org.lukasj.idea.torquescript.psi.TSTypes


class TSSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = TSLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = pack(ATTRIBUTES[tokenType])

    companion object {
        private val ATTRIBUTES = HashMap<IElementType, TextAttributesKey>()

        init
        {
            fillMap(ATTRIBUTES, LINE_COMMENT, TSTypes.LINE_COMMENT)
            fillMap(ATTRIBUTES, DOC_COMMENT, TSTypes.DOC_COMMENT)
            fillMap(ATTRIBUTES, BLOCK_COMMENT, TSTypes.BLOCK_COMMENT)
            fillMap(ATTRIBUTES, PARENTHESES, TSTypes.LEFT_PAREN, TSTypes.RIGHT_PAREN)
            fillMap(ATTRIBUTES, BRACES, TSTypes.LBRACE, TSTypes.RBRACE)
            fillMap(ATTRIBUTES, BRACKETS, TSTypes.LEFT_BRACK, TSTypes.RIGHT_BRACK)
            fillMap(ATTRIBUTES, BAD_CHARACTER, TokenType.BAD_CHARACTER)
            fillMap(ATTRIBUTES, LOCALVAR, TSTypes.LOCALVAR)
            fillMap(ATTRIBUTES, THISVAR, TSTypes.THISVAR)
            fillMap(ATTRIBUTES, GLOBALVAR, TSTypes.GLOBALVAR)
            fillMap(ATTRIBUTES, IDENTIFIER, TSTypes.IDENT)
            fillMap(ATTRIBUTES, DOT, TSTypes.DOT)
            fillMap(ATTRIBUTES, COLON, TSTypes.COLON)
            fillMap(ATTRIBUTES, SEMICOLON, TSTypes.STMT_SEPARATOR)
            fillMap(ATTRIBUTES, COMMA, TSTypes.COMMA)
            fillMap(ATTRIBUTES, STRING, TSTypes.QUOTED_STRING)
            fillMap(ATTRIBUTES, TAG, TSTypes.TAGGED_STRING)
            fillMap(ATTRIBUTES, TSParserDefinition.OPERATORS, OPERATOR)
            fillMap(ATTRIBUTES, TSParserDefinition.KEYWORDS, KEYWORD)
            fillMap(ATTRIBUTES, TSParserDefinition.NUMBERS, NUMBER)
        }
    }
}