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
import org.lukasj.idea.torquescript.TslLanguage
import org.lukasj.idea.torquescript.lexer.TslLexerAdapter
import org.lukasj.idea.torquescript.psi.TslFile
import org.lukasj.idea.torquescript.psi.TslTypes
import org.lukasj.idea.torquescript.psi.TslTypes.*

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
            BLUEPRINT, STRUCT, VERTEX_SHADER, PIXEL_SHADER, PRAGMA, UNIFORM, RETURN,
            IF, ELSE, DO, WHILE, FOR, SWITCH, CASE, DEFAULT, BREAK, CONTINUE, DISCARD,
            FLOAT, FLOAT2, FLOAT3, FLOAT4, FLOAT2X2,
            FLOAT3X3, FLOAT3X4, FLOAT4X3, FLOAT4X4,
            VEC2, VEC3, VEC4, MAT2X2, MAT3X3, MAT3X4, MAT4X3, MAT4X4,
            SAMPLER1D, SAMPLER2D, SAMPLER3D, SAMPLER1DSHADOW, SAMPLER2DSHADOW,
            SAMPLER2DARRAY, SAMPLERCUBE, SAMPLERCUBEARRAY
        )

        val OPERATORS = TokenSet.create(
            PLUS, MINUS, DIVIDE, MODULO, MULTIPLY,
            AND, OR, NOT,
            BIT_AND, BIT_OR, BIT_NOT, BIT_SHIFT_LEFT, BIT_SHIFT_RIGHT
        )

        val SEMANTICS = TokenSet.create(
            POSITION, BINORMAL, NORMAL, COLOR, TEXCOORD,
            SV_POSITION, SV_TARGET, SV_DEPTH
        )

        val STRUCT_TYPES = TokenSet.create(
            STRUCT_CONNECTDATA, STRUCT_VERTDATA, STRUCT_FRAGOUT
        )

        val ASSIGNMENT_OPERATORS = TokenSet.create(
            ADD_ASSIGN, SUBTRACT_ASSIGN, DIVIDE_ASSIGN, MULTIPLY_ASSIGN, MODULO_ASSIGN,
            BIT_AND_ASSIGN, BIT_OR_ASSIGN, BIT_XOR_ASSIGN, BIT_SHIFT_LEFT_ASSIGN, BIT_SHIFT_RIGHT_ASSIGN
        )

        val NUMBERS = TokenSet.create(
        )
    }
}