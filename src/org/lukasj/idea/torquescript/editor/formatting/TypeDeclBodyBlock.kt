package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import org.lukasj.idea.torquescript.psi.TSTypes

class TypeDeclBodyBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    indent: Indent?,
    spacingBuilder: SpacingBuilder
) : NodeBlock(node, wrap, alignment, indent, spacingBuilder) {
    private val assignAlignment: Alignment = Alignment.createAlignment()

    override fun buildChild(node: ASTNode): List<Block> =
        when (node.elementType) {
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
            TSTypes.STMT_SEPARATOR -> listOf(
                PunctuationBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNormalIndent(),
                    spacingBuilder
                )
            )
            TSTypes.SINGLETON_STATEMENT -> listOf(
                TypeDeclBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNormalIndent(),
                    spacingBuilder
                )
            )
            TSTypes.FIELD_ASSIGNMENT -> listOf(
                FieldAssignmentBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    assignAlignment,
                    Indent.getNormalIndent(),
                    spacingBuilder
                )
            )
            TSTypes.NEW_INSTANCE_EXPRESSION -> listOf(
                NewInstanceBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNormalIndent(),
                    spacingBuilder
                )
            )
            else -> super.buildChild(node)
        }

    override fun getChildIndent(): Indent? {
        return Indent.getNormalIndent()
    }
}