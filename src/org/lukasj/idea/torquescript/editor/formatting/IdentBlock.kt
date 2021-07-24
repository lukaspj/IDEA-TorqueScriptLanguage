package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.Alignment
import com.intellij.formatting.Indent
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode

class IdentBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, indent: Indent?, spacingBuilder: SpacingBuilder) :
    LeafBlock(node, wrap, alignment, indent, spacingBuilder)
