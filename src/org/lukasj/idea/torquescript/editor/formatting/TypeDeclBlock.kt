package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import org.lukasj.idea.torquescript.psi.TSTypes

class TypeDeclBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    indent: Indent?,
    spacingBuilder: SpacingBuilder
) : NodeBlock(node, wrap, alignment, indent, spacingBuilder) {

    override fun buildChild(node: ASTNode): List<Block> =
        when (node.elementType) {
            TSTypes.IDENT -> listOf(
                IdentBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getContinuationIndent(),
                    spacingBuilder
                )
            )
            TSTypes.LEFT_PAREN -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getContinuationIndent(),
                    spacingBuilder
                )
            )
            TSTypes.RIGHT_PAREN -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getContinuationIndent(),
                    spacingBuilder
                )
            )
            TSTypes.OBJECT_NAME -> listOf(
                ExpressionBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getContinuationIndent(),
                    spacingBuilder
                )
            )
            TSTypes.TYPE_DECLARATION_BLOCK -> listOf(
                TypeDeclBodyBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    indent,
                    spacingBuilder
                )
            )
            else -> super.buildChild(node)
        }
}