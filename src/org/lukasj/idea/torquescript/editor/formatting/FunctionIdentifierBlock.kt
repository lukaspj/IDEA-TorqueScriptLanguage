package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import org.lukasj.idea.torquescript.psi.TSTypes

class FunctionIdentifierBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, indent: Indent?, spacingBuilder: SpacingBuilder) :
    NodeBlock(node, wrap, alignment, indent, spacingBuilder) {
    override fun buildChild(node: ASTNode) =
        when (node.elementType) {
            TSTypes.COLON_COLON -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    Alignment.createAlignment(),
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.IDENT -> listOf(
                IdentBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    Alignment.createAlignment(),
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            else -> super.buildChild(node)
        }
}
