package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.formatting.alignment.AlignmentStrategy
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.parser.TSParserDefinition
import org.lukasj.idea.torquescript.psi.*

class TSFormattingBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignmentStrategy: AlignmentStrategy,
    indent: Indent?,
    private val settings: CodeStyleSettings,
    private val spacingBuilder: SpacingBuilder
) : TSAbstractBlock(node, wrap, alignmentStrategy, indent) {

    override fun buildSubBlocks(): List<Block> {
        val strategy = calcAlignmentStrategy()

        return node.getChildren(null)
            .asSequence()
            .onEach { child -> renewAlignmentsIfNecessary(child, strategy) }
            .filter { it.textRange.length > 0 }
            .filter { it.elementType != TokenType.WHITE_SPACE }
            .map { child ->
                buildSubBlock(
                    child,
                    strategy
                )
            }
            .toList()
    }

    private fun renewAlignmentsIfNecessary(child: ASTNode, strategy: CompositeAlignmentStrategy) {
        if (child.elementType == TokenType.WHITE_SPACE && child.text.contains("\n\n")) {
            strategy.renewAlignment(TSTypes.ASSIGNOPERATOR)
        }
    }

    private fun buildSubBlock(child: ASTNode, alignmentStrategy: AlignmentStrategy) =
        TSFormattingBlock(
            child,
            null,
            alignmentStrategy,
            calcIndent(child),
            settings,
            spacingBuilder
        )
            .also { it.subBlocks }

    private fun calcAlignmentStrategy(): CompositeAlignmentStrategy =
        when (node.elementType) {
            TSTypes.NEW_INSTANCE_BLOCK ->
                if (settings.getCommonSettings(TSLanguage.INSTANCE.id).ALIGN_CONSECUTIVE_ASSIGNMENTS) {
                    CompositeAlignmentStrategy(
                        AlignmentStrategy.createAlignmentPerTypeStrategy(
                            listOf(TSTypes.ASSIGNOPERATOR),
                            TSTypes.FIELD_ASSIGNMENT,
                            true
                        )
                    )
                } else {
                    alignmentStrategy
                }
            TSTypes.EXPRESSION_STATEMENT ->
                alignmentStrategy
                    .withAlignmentPerTypeStrategy(
                        listOf(TSTypes.DOT),
                        TSTypes.QUALIFIER_ACCESSOR,
                        true
                    )
            TSTypes.BINARY_EXPRESSION ->
                if (settings.getCommonSettings(TSLanguage.INSTANCE.id).ALIGN_MULTILINE_BINARY_OPERATION) {
                    alignmentStrategy.withNewAllAlignmentIf(
                        TSTypes.BINARY_EXPRESSION,
                        true,
                        node.treeParent.elementType != TSTypes.BINARY_EXPRESSION
                    )
                } else {
                    alignmentStrategy
                }
//
//                alignmentStrategy
//                    .withAlignmentPerTypeStrategy(
//                        listOf(TSTypes.BINARYOPERATOR),
//                        true
//                    )

            else -> alignmentStrategy
        }

    private fun calcIndent(child: ASTNode) =
        when (node.elementType) {
            TSTypes.NEW_INSTANCE_BLOCK -> indentIfNotBrace(child)
            TSTypes.NEW_INSTANCE_EXPRESSION, TSTypes.FUNCTION_DECLARATION -> Indent.getNoneIndent()
            TSTypes.STATEMENT_BLOCK -> indentIfNotBrace(child)
            TSTypes.DEFAULT_BLOCK -> indentIfType(child, TSTypes.STATEMENT)
            TSTypes.CASE_BLOCK -> indentIfType(child, TSTypes.STATEMENT)
            TSTypes.SWITCH_STATEMENT -> indentIfType(child, TSTypes.CASE_BLOCK, TSTypes.DEFAULT_BLOCK)
            TSTypes.STR_SWITCH_STATEMENT -> indentIfType(child, TSTypes.CASE_BLOCK, TSTypes.DEFAULT_BLOCK)
            TSTypes.PACKAGE_DECLARATION -> indentIfType(child, TSTypes.FUNCTION_DECLARATION)
            TSTypes.IF_STATEMENT -> indentIfType(child, TSTypes.STATEMENT)
            TSTypes.ELSE_STATEMENT -> indentIfType(child, TSTypes.STATEMENT)
            TSTypes.FOR_STATEMENT -> indentIfType(child, TSTypes.STATEMENT)
            TSTypes.FOREACH_STATEMENT -> indentIfType(child, TSTypes.STATEMENT)
            TSTypes.STR_FOREACH_STATEMENT -> indentIfType(child, TSTypes.STATEMENT)
            TSTypes.DO_WHILE_STATEMENT -> indentIfType(child, TSTypes.STATEMENT)
            TSTypes.WHILE_STATEMENT -> indentIfType(child, TSTypes.STATEMENT)
            TSParserDefinition.FILE -> Indent.getNoneIndent()
            else -> Indent.getContinuationWithoutFirstIndent()
        }

    private fun indentIfType(child: ASTNode, vararg types: IElementType) =
        types
            .mapNotNull {
                when (child.elementType) {
                    it -> Indent.getNormalIndent()
                    TSTypes.LINE_COMMENT, TSTypes.BLOCK_COMMENT, TSTypes.DOC_COMMENT -> Indent.getNormalIndent()
                    else -> null
                }
            }
            .firstOrNull() ?: Indent.getNoneIndent()

    private fun indentIfNotBrace(child: ASTNode): Indent =
        if (BRACES_TOKEN_SET.contains(child.elementType)) {
            Indent.getNoneIndent()
        } else {
            Indent.getNormalIndent()
        }

    override fun getChildAttributes(newChildIndex: Int) =
        ChildAttributes(
            when (node.elementType) {
                TSTypes.STATEMENT_BLOCK, TSTypes.NEW_INSTANCE_BLOCK -> Indent.getNormalIndent()
                TSTypes.IF_STATEMENT ->
                    if (newChildIndex > 3) {
                        Indent.getNormalIndent()
                    } else {
                        Indent.getContinuationIndent()
                    }
                TSParserDefinition.FILE ->
                    (subBlocks.getOrNull(newChildIndex - 1) as TSFormattingBlock).node
                        .let { prevNode ->
                            if (prevNode.firstChildNode?.firstChildNode?.elementType == TSTypes.IF_STATEMENT && !prevNode.textContains(
                                    '{'
                                )
                            ) {
                                Indent.getNormalIndent()
                            } else {
                                Indent.getNoneIndent()
                            }
                        }
                else -> Indent.getContinuationIndent()
            },
            null
        )

    override fun getSpacing(child1: Block?, child2: Block) =
        if (child1 is TSAbstractBlock && child2 is TSAbstractBlock) {
            if (child1.node.elementType == TSTypes.BINARYOPERATOR) {
                if (isSpaceAroundEnabledForType(child1.node.firstChildNode.elementType)) {
                    oneSpace();
                } else {
                    null
                }
            } else if (child2.node.elementType == TSTypes.BINARYOPERATOR) {
                if (isSpaceAroundEnabledForType(child2.node.firstChildNode.elementType)) {
                    oneSpace();
                } else {
                    null
                }
            } else {
                null
            }
        } else {
            null
        } ?: spacingBuilder.getSpacing(this, child1, child2)

    private fun oneSpace() =
        settings.getCommonSettings(TSLanguage.INSTANCE.id)
            .let { commonSettings ->
                Spacing.createSpacing(1, 1, 0, commonSettings.KEEP_LINE_BREAKS, commonSettings.KEEP_BLANK_LINES_IN_CODE)
            }

    private fun isSpaceAroundEnabledForType(elementType: IElementType) =
        settings.getCommonSettings(TSLanguage.INSTANCE.id)
            .let { commonSettings ->
                when (elementType) {
                    TSTypes.PLUS, TSTypes.MINUS, TSTypes.CONCATENATE, TSTypes.SPC, TSTypes.TAB, TSTypes.NL -> commonSettings.SPACE_AROUND_ADDITIVE_OPERATORS
                    TSTypes.MULTIPLY, TSTypes.DIVIDE, TSTypes.MODULO -> commonSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS
                    TSTypes.BIT_AND, TSTypes.BIT_OR, TSTypes.BIT_XOR -> commonSettings.SPACE_AROUND_BITWISE_OPERATORS
                    TSTypes.LT, TSTypes.LT_EQUAL, TSTypes.GT, TSTypes.GT_EQUAL -> commonSettings.SPACE_AROUND_RELATIONAL_OPERATORS
                    TSTypes.EQUAL, TSTypes.NOT_EQUAL -> commonSettings.SPACE_AROUND_EQUALITY_OPERATORS
                    TSTypes.AND, TSTypes.OR -> commonSettings.SPACE_AROUND_LOGICAL_OPERATORS
                    TSTypes.BIT_SHIFT_LEFT, TSTypes.BIT_SHIFT_RIGHT -> commonSettings.SPACE_AROUND_SHIFT_OPERATORS
                    else -> false
                }
            }

    companion object {
        private val BRACES_TOKEN_SET = TokenSet.create(
            TSTypes.LPAREN,
            TSTypes.RPAREN,
            TSTypes.LBRACK,
            TSTypes.RBRACK,
            TSTypes.LBRACE,
            TSTypes.RBRACE
        )
    }
}