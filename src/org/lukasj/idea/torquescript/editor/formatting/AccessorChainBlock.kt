package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import org.lukasj.idea.torquescript.psi.TSTypes

class AccessorChainBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    indent: Indent?,
    spacingBuilder: SpacingBuilder
) : NodeBlock(node, wrap, alignment, indent, spacingBuilder) {
    override fun buildChild(node: ASTNode): List<Block> =
        when (node.elementType) {
            TSTypes.ARGUMENTS -> buildChildren(node)
            TSTypes.PROPERTY -> listOf(
                IdentBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.ACCESSOR_CHAIN -> buildChildren(node)
            TSTypes.QUALIFIER_ACCESSOR ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        when (it.elementType) {
                            TSTypes.PROPERTY -> listOf(
                                IdentBlock(
                                    it,
                                    Wrap.createWrap(WrapType.NONE, false),
                                    null,
                                    Indent.getNoneIndent(),
                                    spacingBuilder
                                )
                            )
                            TSTypes.ACCESSOR_CHAIN -> listOf(
                                AccessorChainBlock(
                                    it,
                                    Wrap.createWrap(WrapType.NONE, false),
                                    alignment,
                                    Indent.getContinuationIndent(),
                                    spacingBuilder
                                )
                            )
                            else -> super.buildChild(it)
                        }
                    }
            TSTypes.CALL_ACCESSOR ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        when (it.elementType) {
                            TSTypes.ARGUMENTS ->
                                it.getChildren(null)
                                    .filter { arg -> arg.elementType != TokenType.WHITE_SPACE }
                                    .flatMap { arg ->
                                        buildChild(arg)
                                    }
                            TSTypes.ACCESSOR_CHAIN -> listOf(
                                AccessorChainBlock(
                                    it,
                                    Wrap.createWrap(WrapType.NONE, false),
                                    alignment,
                                    Indent.getContinuationIndent(),
                                    spacingBuilder
                                )
                            )
                            else -> super.buildChild(it)
                        }
                    }
            TSTypes.INDEX_ACCESSOR ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        when (it.elementType) {
                            TSTypes.ARGUMENTS ->
                                it.getChildren(null)
                                    .filter { arg -> arg.elementType != TokenType.WHITE_SPACE }
                                    .flatMap { arg ->
                                        buildChild(arg)
                                    }
                            TSTypes.ACCESSOR_CHAIN -> listOf(
                                AccessorChainBlock(
                                    it,
                                    Wrap.createWrap(WrapType.NONE, false),
                                    alignment,
                                    Indent.getContinuationIndent(),
                                    spacingBuilder
                                )
                            )
                            else -> super.buildChild(it)
                        }
                    }
            else -> super.buildChild(node)
        }
}
