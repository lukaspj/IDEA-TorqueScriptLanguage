package org.lukasj.idea.torquescript.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.lexer.TSLexerAdapter
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.TSTypes.*

class TSParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?) = TSLexerAdapter()

    override fun createParser(project: Project?) = TSParser()

    override fun getFileNodeType() = FILE

    override fun getCommentTokens() = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode?): PsiElement = Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = TSFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements =
        ParserDefinition.SpaceRequirements.MAY

    companion object {
        @JvmField
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)

        @JvmField
        val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT, DOC_COMMENT)

        @JvmField
        val FILE = IFileElementType(TSLanguage.INSTANCE)

        val KEYWORDS = TokenSet.create(
            DATABLOCK, SINGLETON, FUNCTION, PACKAGE, NAMESPACE,
            DO, WHILE, FOR, FOREACH, STR_FOREACH, SWITCH, STR_SWITCH, IF, ELSE,
            CASE, DEFAULT, BREAK, CONTINUE, RETURN, IN,
            NEW, ASSERT, TRUE, FALSE
        )

        val OPERATORS = TokenSet.create(
            ASSIGN, ADD_ASSIGN, SUBTRACT_ASSIGN, MULTIPLY_ASSIGN, DIVIDE_ASSIGN,
            MODULO_ASSIGN, BIT_AND_ASSIGN, BIT_XOR_ASSIGN, BIT_OR_ASSIGN,
            BIT_SHIFT_LEFT_ASSIGN, BIT_SHIFT_RIGHT_ASSIGN,

            EQUAL, NOT_EQUAL, GT_EQUAL, GT, LT_EQUAL, LT, STR_EQUAL, STR_NOT_EQUAL,
            AND, OR, CONCATENATE, PLUS, MINUS, MULTIPLY, DIVIDE, MODULO, NL, TAB, SPC,
            BIT_SHIFT_LEFT, BIT_SHIFT_RIGHT, BIT_AND, BIT_OR, BIT_XOR, BIT_NOT,
            COLON_COLON, INTERNAL_NAME, INTERNAL_NAME_RECURSIVE, DECREMENT, INCREMENT, NOT
        )

        val NUMBERS = TokenSet.create(
            FLOAT, INTEGER, HEXDIGIT
        )
    }
}