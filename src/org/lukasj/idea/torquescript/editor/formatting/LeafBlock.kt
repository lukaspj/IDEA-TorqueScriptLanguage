package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.formatter.common.AbstractBlock

abstract class LeafBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    protected val _indent: Indent?,
    protected val spacingBuilder: SpacingBuilder
) : AbstractBlock(node, wrap, alignment) {

    override fun getSpacing(child1: Block?, child2: Block) =
        spacingBuilder.getSpacing(this, child1, child2)

    override fun isLeaf() = true

    override fun getIndent() = _indent

    override fun buildChildren(): List<Block> = listOf()
}