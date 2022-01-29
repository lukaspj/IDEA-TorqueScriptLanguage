package org.lukasj.idea.torquescript.editor

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.lukasj.idea.torquescript.psi.TSStatementBlock
import org.lukasj.idea.torquescript.psi.TSSwitchStatement
import org.lukasj.idea.torquescript.psi.TSTypes

class TSFoldingBuilder : FoldingBuilderEx() {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()

        // Generic block statements are easy
        PsiTreeUtil.findChildrenOfType(root, TSStatementBlock::class.java)
            .filter { it.firstChild.node.elementType == TSTypes.LBRACE }
            .filter { it.firstChild.textOffset != it.lastChild.textOffset }
            .filter { it.firstChild.textOffset + 1 != it.lastChild.textOffset }
            .forEach { block ->
                descriptors.add(
                    object : FoldingDescriptor(
                        block.node,
                        TextRange(block.firstChild.textOffset + 1, block.lastChild.textOffset),
                        null
                    ) {
                        override fun getPlaceholderText() = "..."
                    }
                )
            }

        // Switch needs to be handled specially
        PsiTreeUtil.findChildrenOfType(root, TSSwitchStatement::class.java)
            .forEach { switch ->
                switch.node.getChildren(TokenSet.create(TSTypes.LBRACE))
                    .firstOrNull()?.let { blockStart ->
                        switch.node.getChildren(TokenSet.create(TSTypes.RBRACE))
                            .firstOrNull()?.let { blockEnd ->
                                descriptors.add(
                                    object : FoldingDescriptor(
                                        blockStart,
                                        TextRange(
                                            blockStart.startOffset + 1,
                                            blockEnd.startOffset
                                        ),
                                        null
                                    ) {
                                        override fun getPlaceholderText() = "..."
                                    }
                                )
                            }
                    }
            }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode) = "..."

    override fun isCollapsedByDefault(node: ASTNode) = false
}