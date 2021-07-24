package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import org.lukasj.idea.torquescript.psi.TSTypes

class DeclarationBlock(node: ASTNode, wrap: Wrap?, alignment: Alignment?, indent: Indent?, spacingBuilder: SpacingBuilder) :
    NodeBlock(node, wrap, alignment, indent, spacingBuilder) {
    override fun buildChild(node: ASTNode) =
        when (node.elementType) {
            TSTypes.FUNCTION_DECLARATION ->
                listOf(
                    FunctionDeclBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    )
                )
            TSTypes.PACKAGE_DECLARATION ->
                listOf(
                    PackageDeclBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    )
                )
            TSTypes.STATEMENT ->
                listOf(
                    StatementBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    )
                )
            TSTypes.STMT_SEPARATOR ->
                listOf(
                    PunctuationBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    )
                )
            else -> super.buildChild(node)
        }

}
