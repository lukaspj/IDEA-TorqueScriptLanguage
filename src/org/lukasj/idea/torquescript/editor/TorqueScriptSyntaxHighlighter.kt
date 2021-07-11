package org.lukasj.idea.torquescript.editor

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.lukasj.idea.torquescript.parser.TorqueScriptParserDefinition
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.BAD_CHARACTER
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.BLOCK_COMMENT
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.BRACES
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.BRACKETS
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.COLON
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.COMMA
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.DOC_COMMENT
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.DOT
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.GLOBALVAR
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.IDENTIFIER
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.KEYWORD
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.LINE_COMMENT
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.LOCALVAR
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.NUMBER
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.OPERATOR
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.PARENTHESES
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.SEMICOLON
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.STRING
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.TAG
import org.lukasj.idea.torquescript.editor.TorqueScriptSyntaxHighlightingColors.THISVAR
import org.lukasj.idea.torquescript.lexer.TorqueScriptLexerAdapter
import org.lukasj.idea.torquescript.psi.TorqueScriptTypes


class TorqueScriptSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = TorqueScriptLexerAdapter()

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
            fillMap(ATTRIBUTES, TorqueScriptParserDefinition.OPERATORS, OPERATOR)
            fillMap(ATTRIBUTES, TorqueScriptParserDefinition.KEYWORDS, KEYWORD)
            fillMap(ATTRIBUTES, TorqueScriptParserDefinition.NUMBERS, NUMBER)
        }
    }
}