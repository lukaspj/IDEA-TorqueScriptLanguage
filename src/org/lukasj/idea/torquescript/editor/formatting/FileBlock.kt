package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import org.lukasj.idea.torquescript.psi.TSTypes

class FileBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, spacingBuilder: SpacingBuilder) :
    NodeBlock(
        node,
        wrap,
        alignment,
        Indent.getNoneIndent(),
        spacingBuilder
    ) {
    override fun buildChild(node: ASTNode) =
        when (node.elementType) {
            TSTypes.DECLARATION ->
                listOf(
                    DeclarationBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder,
                    )
                )
            TSTypes.LINE_COMMENT ->
                listOf(
                    CommentBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    )
                )
            TSTypes.BLOCK_COMMENT ->
                listOf(
                    CommentBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    )
                )
            TSTypes.STMT_SEPARATOR ->
                listOf(
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

    override fun getChildIndent(): Indent = Indent.getNoneIndent()
}