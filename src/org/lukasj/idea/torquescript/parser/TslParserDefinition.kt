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
import org.lukasj.idea.torquescript.TslLanguage
import org.lukasj.idea.torquescript.lexer.TslLexerAdapter
import org.lukasj.idea.torquescript.psi.TslTypes.*
import org.lukasj.idea.torquescript.psi.TslFile

class TslParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?) = TslLexerAdapter()

    override fun createParser(project: Project?) = TslParser()

    override fun getFileNodeType() = FILE

    override fun getCommentTokens() = COMMENTS

    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY

    override fun createElement(node: ASTNode?): PsiElement = Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = TslFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements =
        ParserDefinition.SpaceRequirements.MAY

    companion object {
        @JvmField
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)

        @JvmField
        val COMMENTS = TokenSet.create(LINE_COMMENT)

        @JvmField
        val FILE = IFileElementType(TslLanguage.INSTANCE)

        val KEYWORDS = TokenSet.create(
            TORQUESHADER, STRUCT, UNIFORM, // CBUFFER,
            VERTEX_SHADER, PIXEL_SHADER, GEOMETRY_SHADER, COMPUTE_SHADER,
            MAT3X4, MAT4X3, MAT3X3, MAT4X4,
            FVEC2, FVEC3, FVEC4, IVEC2, IVEC3, IVEC4, BVEC2, BVEC3, BVEC4,
            FLOAT, INT, UINT, BOOL, SAMPLER2D,
            IF, ELSE, WHILE, DO, BREAK, SWITCH, CASE, DEFAULT, CONTINUE, DISCARD,
            VOID, STATIC, CONST, IN, OUT, INOUT,
            RETURN,
        )

        val OPERATORS = TokenSet.create(
            PLUS, MINUS, DIVIDE, MODULO, MULTIPLY,
            AND, OR, NOT,
            BIT_AND, BIT_OR, BIT_NOT, BIT_SHIFT_LEFT, BIT_SHIFT_RIGHT
        )

        val SEMANTICS = TokenSet.create(
            BINORMAL, NORMAL, TANGENT, TANGENTW, COLOR,
            TARGET, POSITION, SV_POSITION, SV_DEPTH, SV_ISFRONTFACE,
            PSIZE, TESSFACTOR
        )

        val INTRINSICFUNCTIONS = TokenSet.create(
            SAMPLEFUNC, MULFUNC, FRACFUNC, LERPFUNC
        )

        val ASSIGNMENT_OPERATORS = TokenSet.create(
            ADD_ASSIGN, SUBTRACT_ASSIGN, DIVIDE_ASSIGN, MULTIPLY_ASSIGN, MODULO_ASSIGN,
            BIT_AND_ASSIGN, BIT_OR_ASSIGN, BIT_XOR_ASSIGN, BIT_SHIFT_LEFT_ASSIGN, BIT_SHIFT_RIGHT_ASSIGN
        )

        val NUMBERS = TokenSet.create(
            LITERAL_FLOAT, LITERAL_DOUBLE, LITERAL_INTEGER, LITERAL_HEXDIGIT
        )

        val BOOLEANS = TokenSet.create(TRUE, FALSE)
    }
}