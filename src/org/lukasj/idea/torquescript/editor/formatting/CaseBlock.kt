package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import org.lukasj.idea.torquescript.psi.TSTypes

class CaseBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, indent: Indent?, spacingBuilder: SpacingBuilder) :
    NodeBlock(node, wrap, alignment, indent, spacingBuilder) {
    override fun buildChild(node: ASTNode): List<Block> =
        when (node.elementType) {
            TSTypes.COLON -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.STATEMENT -> listOf(
                StatementBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNormalIndent(),
                    spacingBuilder
                )
            )
            else -> super.buildChild(node)
        }
}
