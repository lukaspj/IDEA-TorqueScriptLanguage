package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode

class KeywordBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, indent: Indent?, spacingBuilder: SpacingBuilder) :
    LeafBlock(node, wrap, alignment, indent, spacingBuilder)
