package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.lukasj.idea.torquescript.TSLanguage

class TSFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(context: FormattingContext): FormattingModel =
        FormattingModelProvider
            .createFormattingModelForPsiFile(
                context.containingFile,
                FileBlock(
                    context.node,
                    Wrap.createWrap(WrapType.NONE, false),
                    null,
                    createSpaceBuilder(context.codeStyleSettings)
                ),
                context.codeStyleSettings
            )

    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder =
        SpacingBuilder(settings, TSLanguage.INSTANCE)


    override fun getRangeAffectingIndent(file: PsiFile?, offset: Int, elementAtOffset: ASTNode?): TextRange? {
        return super.getRangeAffectingIndent(file, offset, elementAtOffset)
    }
}