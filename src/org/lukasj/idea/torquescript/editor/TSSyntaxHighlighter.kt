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
import org.lukasj.idea.torquescript.psi.TorqueScriptTypes


class TSSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = TSLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = pack(ATTRIBUTES[tokenType])

    companion object {
        private val ATTRIBUTES = HashMap<IElementType, TextAttributesKey>()

        init
        {
            fillMap(ATTRIBUTES, LINE_COMMENT, TorqueScriptTypes.LINE_COMMENT)
            fillMap(ATTRIBUTES, DOC_COMMENT, TorqueScriptTypes.DOC_COMMENT)
            fillMap(ATTRIBUTES, BLOCK_COMMENT, TorqueScriptTypes.BLOCK_COMMENT)
            fillMap(ATTRIBUTES, PARENTHESES, TorqueScriptTypes.LEFT_PAREN, TorqueScriptTypes.RIGHT_PAREN)
            fillMap(ATTRIBUTES, BRACES, TorqueScriptTypes.LBRACE, TorqueScriptTypes.RBRACE)
            fillMap(ATTRIBUTES, BRACKETS, TorqueScriptTypes.LEFT_BRACK, TorqueScriptTypes.RIGHT_BRACK)
            fillMap(ATTRIBUTES, BAD_CHARACTER, TokenType.BAD_CHARACTER)
            fillMap(ATTRIBUTES, LOCALVAR, TorqueScriptTypes.LOCALVAR)
            fillMap(ATTRIBUTES, THISVAR, TorqueScriptTypes.THISVAR)
            fillMap(ATTRIBUTES, GLOBALVAR, TorqueScriptTypes.GLOBALVAR)
            fillMap(ATTRIBUTES, IDENTIFIER, TorqueScriptTypes.IDENT)
            fillMap(ATTRIBUTES, DOT, TorqueScriptTypes.DOT)
            fillMap(ATTRIBUTES, COLON, TorqueScriptTypes.COLON)
            fillMap(ATTRIBUTES, SEMICOLON, TorqueScriptTypes.STMT_SEPARATOR)
            fillMap(ATTRIBUTES, COMMA, TorqueScriptTypes.COMMA)
            fillMap(ATTRIBUTES, STRING, TorqueScriptTypes.QUOTED_STRING)
            fillMap(ATTRIBUTES, TAG, TorqueScriptTypes.TAGGED_STRING)
            fillMap(ATTRIBUTES, TSParserDefinition.OPERATORS, OPERATOR)
            fillMap(ATTRIBUTES, TSParserDefinition.KEYWORDS, KEYWORD)
            fillMap(ATTRIBUTES, TSParserDefinition.NUMBERS, NUMBER)
        }
    }
}