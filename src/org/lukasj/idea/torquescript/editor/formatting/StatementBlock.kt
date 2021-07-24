package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import org.lukasj.idea.torquescript.psi.TSTypes

class StatementBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    indent: Indent?,
    spacingBuilder: SpacingBuilder
) :
    NodeBlock(node, wrap, alignment, indent, spacingBuilder) {

    override fun buildChild(node: ASTNode): List<Block> =
        when (node.elementType) {
            TSTypes.IF_STATEMENT -> listOf(
                IfStatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.SWITCH_STATEMENT -> listOf(
                SwitchStatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.STR_SWITCH_STATEMENT -> listOf(
                SwitchStatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.FOR_STATEMENT -> listOf(
                LoopStatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.FOREACH_STATEMENT -> listOf(
                LoopStatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.STR_FOREACH_STATEMENT -> listOf(
                LoopStatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.DO_WHILE_STATEMENT -> listOf(
                LoopStatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.WHILE_STATEMENT -> listOf(
                LoopStatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.BREAK_STATEMENT -> listOf(
                KeywordBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.CONTINUE_STATEMENT -> listOf(
                KeywordBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.RETURN_STATEMENT -> listOf(
                object : NodeBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                ) {
                    override fun buildChild(node: ASTNode): List<Block> = listOf()
                }
            )
            TSTypes.ASSERT_STATEMENT -> listOf(
                object : NodeBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                ) {
                    override fun buildChild(node: ASTNode): List<Block> = listOf()
                }
            )
            TSTypes.EXPRESSION_STATEMENT -> listOf(
                ExpressionBlock(
                    node.firstChildNode,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.SINGLETON_STATEMENT -> listOf(
                TypeDeclBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.DATABLOCK_STATEMENT -> listOf(
                TypeDeclBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.STMT_SEPARATOR -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            else -> super.buildChild(node)
        }
}
