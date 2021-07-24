package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.Alignment
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode

class ParamBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, spacingBuilder: SpacingBuilder) :
    LeafBlock(node, wrap, alignment, null, spacingBuilder)
