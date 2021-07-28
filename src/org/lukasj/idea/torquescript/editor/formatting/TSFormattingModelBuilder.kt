package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.formatting.alignment.AlignmentStrategy
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.psi.tree.TokenSet
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.psi.TSTypes

class TSFormattingModelBuilder : FormattingModelBuilder {
    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder =
        settings.getCommonSettings(TSLanguage.INSTANCE.id)
            .let { commonSettings ->
                SpacingBuilder(settings, TSLanguage.INSTANCE)
                    .after(TokenSet.create(TSTypes.LINE_COMMENT, TSTypes.DOC_COMMENT)).lineBreakInCode()
                    .around(TSTypes.NEW_INSTANCE_BLOCK).lineBreakOrForceSpace(
                        commonSettings.CLASS_BRACE_STYLE >= CommonCodeStyleSettings.NEXT_LINE,
                        true
                    )
                    .aroundInside(TSTypes.STATEMENT_BLOCK, TSTypes.FUNCTION_DECLARATION).lineBreakOrForceSpace(
                        commonSettings.METHOD_BRACE_STYLE >= CommonCodeStyleSettings.NEXT_LINE,
                        true
                    )
                    .beforeInside(TSTypes.LBRACE, TSTypes.PACKAGE_DECLARATION).lineBreakOrForceSpace(
                        settings.getCustomSettings(TSCodeStyleSettings::class.java).PACKAGE_BRACE_STYLE >= CommonCodeStyleSettings.NEXT_LINE,
                        true
                    )
                    .beforeInside(TokenSet.create(TSTypes.STATEMENT_BLOCK, TSTypes.STATEMENT), TSTypes.ELSE_STATEMENT).lineBreakOrForceSpace(
                        commonSettings.BRACE_STYLE >= CommonCodeStyleSettings.NEXT_LINE,
                        commonSettings.SPACE_BEFORE_ELSE_LBRACE
                    )
                    .before(TSTypes.STATEMENT_BLOCK).lineBreakOrForceSpace(
                        commonSettings.BRACE_STYLE >= CommonCodeStyleSettings.NEXT_LINE,
                        true
                    )
                    .before(TSTypes.LBRACE).lineBreakOrForceSpace(
                        commonSettings.BRACE_STYLE >= CommonCodeStyleSettings.NEXT_LINE,
                        true
                    )
                    .around(TSTypes.ASSIGNOPERATOR).spaceIf(commonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                    .beforeInside(TSTypes.LPAREN, TSTypes.IF_STATEMENT).spaceIf(commonSettings.SPACE_BEFORE_IF_PARENTHESES)
                    .beforeInside(TSTypes.STATEMENT_BLOCK, TSTypes.IF_STATEMENT).spaceIf(commonSettings.SPACE_BEFORE_IF_LBRACE)
                    .afterInside(TSTypes.LPAREN, TSTypes.IF_STATEMENT).spaceIf(commonSettings.SPACE_WITHIN_IF_PARENTHESES)
                    .beforeInside(TSTypes.RPAREN, TSTypes.IF_STATEMENT).spaceIf(commonSettings.SPACE_WITHIN_IF_PARENTHESES)
                    .afterInside(TSTypes.STATEMENT_BLOCK, TSTypes.IF_STATEMENT).lineBreakOrForceSpace(
                        commonSettings.ELSE_ON_NEW_LINE,
                        commonSettings.SPACE_BEFORE_ELSE_KEYWORD
                    )
                    .after(TSTypes.ELSE).spaceIf(commonSettings.SPACE_BEFORE_ELSE_LBRACE)
                    .before(TSTypes.COMMA).spaceIf(commonSettings.SPACE_BEFORE_COMMA)
                    .after(TSTypes.COMMA).spaceIf(commonSettings.SPACE_AFTER_COMMA)
                    .beforeInside(TSTypes.COLON, TSTypes.CASE_BLOCK).spaces(0)
                    .before(TSTypes.COLON).spaceIf(commonSettings.SPACE_BEFORE_COLON)
                    .after(TSTypes.COLON).spaceIf(commonSettings.SPACE_AFTER_COLON)
                    .after(TSTypes.LBRACE).lineBreakInCode()
                    .around(TSTypes.RBRACE).lineBreakInCode()
                    .around(
                        TokenSet.create(TSTypes.POSTFIX_UNARY_EXPRESSION, TSTypes.PREFIX_UNARY_EXPRESSION)
                    ).spaceIf(commonSettings.SPACE_AROUND_UNARY_OPERATOR)
                    .before(TSTypes.LPAREN).none()
            }

    override fun createModel(formattingContext: FormattingContext) =
        FormattingModelProvider
            .createFormattingModelForPsiFile(
                formattingContext.containingFile,
                TSFormattingBlock(
                    formattingContext.node,
                    null,
                    AlignmentStrategy.getNullStrategy(),
                    Indent.getNoneIndent(),
                    formattingContext.codeStyleSettings,
                    createSpaceBuilder(formattingContext.codeStyleSettings)
                ),
                formattingContext.codeStyleSettings
            )
}

