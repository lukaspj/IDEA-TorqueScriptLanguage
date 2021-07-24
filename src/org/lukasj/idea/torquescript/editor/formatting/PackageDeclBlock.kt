package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import org.lukasj.idea.torquescript.psi.TSTypes

class PackageDeclBlock(
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
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )

            TSTypes.LBRACE -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.RBRACE -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )

            TSTypes.FUNCTION_DECLARATION -> listOf(
                FunctionDeclBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNormalIndent(),
                    spacingBuilder
                )
            )
            else -> super.buildChild(node)
        }
}