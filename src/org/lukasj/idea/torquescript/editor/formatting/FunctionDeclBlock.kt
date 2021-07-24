package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import org.lukasj.idea.torquescript.psi.TSTypes

class FunctionDeclBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    indent: Indent?,
    spacingBuilder: SpacingBuilder
) : NodeBlock(node, wrap, alignment, indent, spacingBuilder) {

    override fun buildChild(node: ASTNode): List<Block> =
        when (node.elementType) {
            TSTypes.FUNCTION -> listOf(
                KeywordBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.FUNCTION_IDENTIFIER -> listOf(
                FunctionIdentifierBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.LEFT_PAREN -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.RIGHT_PAREN -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.THIS_VAR_EXPRESSION -> listOf(
                ParamBlock(
                    node.firstChildNode,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    spacingBuilder
                )
            )
            TSTypes.LOCAL_VAR_EXPRESSION -> listOf(
                ParamBlock(
                    node.firstChildNode,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    spacingBuilder
                )
            )
            TSTypes.COMMA -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.STATEMENT_BLOCK -> listOf(
                StatementListBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    indent,
                    spacingBuilder
                )
            )
            TSTypes.PARAMS ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap { buildChild(it) }
            else -> super.buildChild(node)
        }
}