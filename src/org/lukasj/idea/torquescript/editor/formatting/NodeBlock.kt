package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock
import org.lukasj.idea.torquescript.parser.TSParserDefinition
import org.lukasj.idea.torquescript.psi.TSTypes

abstract class NodeBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    protected val _indent: Indent?,
    protected val spacingBuilder: SpacingBuilder
) : AbstractBlock(node, wrap, alignment) {

    override fun getSpacing(child1: Block?, child2: Block) =
        spacingBuilder.getSpacing(this, child1, child2)

    override fun getIndent() = _indent

    override fun isLeaf() = myNode.firstChildNode == null

    override fun buildChildren(): List<Block> = buildChildren(myNode)

    open fun buildChildren(node: ASTNode): List<Block> =
        node.getChildren(null)
            .filter { it.elementType != TokenType.WHITE_SPACE }
            .flatMap {
                buildChild(it)
            }

    open fun buildChild(node: ASTNode): List<Block> =
        if (node.elementType == TokenType.ERROR_ELEMENT
            || node.elementType == TokenType.DUMMY_HOLDER
            || node.elementType == GeneratedParserUtilBase.DUMMY_BLOCK
        ) {
            listOf(
                object : LeafBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                ) {}
            )
        } else if (TSParserDefinition.KEYWORDS.contains(node.elementType)) {
            buildKeyword(node)
        } else if (TSParserDefinition.COMMENTS.contains(node.elementType)) {
            buildComment(node)
        } else if (TSParserDefinition.OPERATORS.contains(node.elementType)
            || node.elementType == TSTypes.ASSIGNOPERATOR
            || node.elementType == TSTypes.BINARYOPERATOR
        ) {
            buildOperator(node)
        } else if (TSParserDefinition.PUNCTUATIONS.contains(node.elementType)) {
            buildPunctuation(node)
        } else {
            buildExpression(node).ifEmpty {
                TODO("Unexpected node in ${myNode.elementType} : ${node.elementType}")
            }
        }

    open fun buildOperator(node: ASTNode): List<Block> =
        listOf(
            object : LeafBlock(
                node,
                Wrap.createWrap(WrapType.NONE, false),
                null,
                Indent.getNoneIndent(),
                spacingBuilder
            ) {}
        )

    open fun buildPunctuation(node: ASTNode): List<Block> =
        listOf(
            PunctuationBlock(
                node,
                Wrap.createWrap(WrapType.NONE, false),
                null,
                Indent.getNoneIndent(),
                spacingBuilder
            )
        )

    open fun buildKeyword(node: ASTNode): List<Block> =
        listOf(
            KeywordBlock(
                node,
                Wrap.createWrap(WrapType.NONE, false),
                null,
                Indent.getNoneIndent(),
                spacingBuilder
            )
        )

    open fun buildComment(node: ASTNode): List<Block> =
        listOf(
            CommentBlock(
                node,
                Wrap.createWrap(WrapType.NONE, false),
                null,
                Indent.getNoneIndent(),
                spacingBuilder
            )
        )

    open fun buildExpression(node: ASTNode): List<Block> =
        when (node.elementType) {
            TSTypes.VAR_EXPRESSION ->
                listOf(
                    object : LeafBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    ) {}
                )
            TSTypes.IDENT_EXPRESSION ->
                listOf(
                    IdentBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    )
                )
            TSTypes.LITERAL_EXPRESSION ->
                listOf(
                    object : LeafBlock(
                        node,
                        Wrap.createWrap(WrapType.NONE, false),
                        null,
                        Indent.getNoneIndent(),
                        spacingBuilder
                    ) {}
                )
            TSTypes.PAREN_EXPRESSION ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        when (it.elementType) {
                            TSTypes.LEFT_PAREN -> listOf(
                                PunctuationBlock(
                                    it,
                                    Wrap.createWrap(WrapType.NONE, false),
                                    null,
                                    Indent.getNoneIndent(),
                                    spacingBuilder
                                )
                            )
                            TSTypes.RIGHT_PAREN -> listOf(
                                PunctuationBlock(
                                    it,
                                    Wrap.createWrap(WrapType.NONE, false),
                                    null,
                                    Indent.getNoneIndent(),
                                    spacingBuilder
                                )
                            )
                            else -> buildExpression(it)
                        }
                    }
            TSTypes.ASSIGNMENT_EXPRESSION ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        when (it.elementType) {
                            TSTypes.ACCESSOR_CHAIN -> listOf(
                                AccessorChainBlock(
                                    it,
                                    Wrap.createWrap(WrapType.NONE, false),
                                    null,
                                    Indent.getContinuationIndent(),
                                    spacingBuilder
                                )
                            )
                            else -> buildChild(it)
                        }
                    }
            TSTypes.TERNARY_EXPRESSION ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        buildChild(it)
                    }
            TSTypes.BINARY_EXPRESSION ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        buildChild(it)
                    }
            TSTypes.NEW_INSTANCE_EXPRESSION -> listOf(
                NewInstanceBlock(
                    node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    Indent.getNoneIndent(),
                    spacingBuilder
                )
            )
            TSTypes.QUALIFIER_EXPRESSION ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        when (it.elementType) {
                            TSTypes.QUALIFIER_ACCESSOR -> buildAccessorChain(it)
                            else -> buildChild(it)
                        }
                    }
            TSTypes.INDEX_EXPRESSION ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        when (it.elementType) {
                            TSTypes.INDEX_ACCESSOR -> buildAccessorChain(it)
                            else -> buildChild(it)
                        }
                    }
            TSTypes.CALL_EXPRESSION ->
                node.getChildren(null)
                    .filter { it.elementType != TokenType.WHITE_SPACE }
                    .flatMap {
                        when (it.elementType) {
                            TSTypes.ARGUMENTS -> buildChildren(it)
                            TSTypes.CALL_ACCESSOR -> buildAccessorChain(it)
                            else -> buildChild(it)
                        }
                    }
            else -> listOf()
        }

    protected fun buildAccessorChain(node: ASTNode) =
        listOf(
            AccessorChainBlock(
                node,
                Wrap.createWrap(WrapType.NONE, false),
                Alignment.createAlignment(),
                Indent.getContinuationIndent(),
                spacingBuilder
            )
        )
}