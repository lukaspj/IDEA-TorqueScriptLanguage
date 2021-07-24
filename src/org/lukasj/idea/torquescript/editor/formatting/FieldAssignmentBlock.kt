package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import org.lukasj.idea.torquescript.psi.TSTypes

class FieldAssignmentBlock(
    node: ASTNode,
    wrap: Wrap?,
    private val assignAlignment: Alignment?,
    indent: Indent?,
    spacingBuilder: SpacingBuilder
) : NodeBlock(node, wrap, assignAlignment, indent, spacingBuilder) {
    override fun buildChild(node: ASTNode) =
        when(node.elementType) {
            TSTypes.PROPERTY -> listOf(
                IdentBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.LEFT_BRACK -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.RIGHT_BRACK -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.COMMA -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.ASSIGN -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    assignAlignment,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            else -> super.buildChild(node)
        }
}
