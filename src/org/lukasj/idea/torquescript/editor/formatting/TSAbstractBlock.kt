package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.formatting.alignment.AlignmentStrategy
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.UserDataHolderBase

abstract class TSAbstractBlock(
    protected val _node: ASTNode,
    protected val _wrap: Wrap?,
    private val _alignmentStrategy: AlignmentStrategy,
    protected val _indent: Indent?
) : UserDataHolderBase(), ASTBlock {
    private var _subBlocks: List<Block>? = null
    private val _alignment = _alignmentStrategy.getAlignment(node.treeParent?.elementType, node.elementType)

    protected val alignmentStrategy
        get() = if (_alignmentStrategy is CompositeAlignmentStrategy) {
            _alignmentStrategy
        } else {
            CompositeAlignmentStrategy(_alignmentStrategy)
        }

    override fun isLeaf() = node.firstChildNode == null

    override fun isIncomplete() = false

    override fun getNode() = _node

    override fun getTextRange(): TextRange = _node.textRange

    override fun getWrap() = _wrap

    override fun getIndent() = _indent

    override fun getAlignment() = _alignment

    override fun getSubBlocks() =
        _subBlocks
            ?: buildSubBlocks()
                .let {
                    _subBlocks = it
                    it
                }


    abstract fun buildSubBlocks(): List<Block>
}