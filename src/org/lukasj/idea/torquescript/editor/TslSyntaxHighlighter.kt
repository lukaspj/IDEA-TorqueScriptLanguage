package org.lukasj.idea.torquescript.editor

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.ASSIGNMENT_OPERATOR
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.BAD_CHARACTER
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.BLOCK_COMMENT
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.BRACES
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.BRACKETS
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.COLON
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.IDENTIFIER
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.KEYWORD
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.LINE_COMMENT
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.NUMBER
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.OPERATOR
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.PARENTHESES
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.SEMANTIC
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.SEMICOLON
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.STRING
import org.lukasj.idea.torquescript.editor.TslSyntaxHighlightingColors.STRUCT_TYPES
import org.lukasj.idea.torquescript.lexer.TslLexerAdapter
import org.lukasj.idea.torquescript.parser.TslParserDefinition
import org.lukasj.idea.torquescript.psi.TslTypes


class TslSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = TslLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = pack(ATTRIBUTES[tokenType])

    companion object {
        private val ATTRIBUTES = HashMap<IElementType, TextAttributesKey>()

        init
        {
            fillMap(ATTRIBUTES, LINE_COMMENT, TslTypes.LINE_COMMENT)
            fillMap(ATTRIBUTES, BLOCK_COMMENT, TslTypes.BLOCK_COMMENT)
            fillMap(ATTRIBUTES, PARENTHESES, TslTypes.LPAREN, TslTypes.RPAREN)
            fillMap(ATTRIBUTES, BRACES, TslTypes.LBRACE, TslTypes.RBRACE)
            fillMap(ATTRIBUTES, BRACKETS, TslTypes.LBRACK, TslTypes.RBRACK)
            fillMap(ATTRIBUTES, BAD_CHARACTER, TokenType.BAD_CHARACTER)
            fillMap(ATTRIBUTES, IDENTIFIER, TslTypes.IDENT)
            fillMap(ATTRIBUTES, COLON, TslTypes.COLON)
            fillMap(ATTRIBUTES, SEMICOLON, TslTypes.STMT_SEPARATOR)
            fillMap(ATTRIBUTES, TslParserDefinition.OPERATORS, OPERATOR)
            fillMap(ATTRIBUTES, TslParserDefinition.KEYWORDS, KEYWORD)
            fillMap(ATTRIBUTES, TslParserDefinition.NUMBERS, NUMBER)
            fillMap(ATTRIBUTES, TslParserDefinition.SEMANTICS, SEMANTIC)
            fillMap(ATTRIBUTES, TslParserDefinition.ASSIGNMENT_OPERATORS, ASSIGNMENT_OPERATOR)
        }
    }
}