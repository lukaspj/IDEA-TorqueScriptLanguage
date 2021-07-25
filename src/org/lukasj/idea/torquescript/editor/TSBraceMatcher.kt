package org.lukasj.idea.torquescript.editor

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.lukasj.idea.torquescript.psi.TSTypes

class TSBraceMatcher : PairedBraceMatcher {
    override fun getPairs(): Array<BracePair> = BRACE_PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?) = true

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int) = openingBraceOffset

    companion object {
        val BRACE_PAIRS = arrayOf(
            BracePair(TSTypes.LPAREN, TSTypes.RPAREN, false),
            BracePair(TSTypes.LBRACK, TSTypes.RBRACK, false),
            BracePair(TSTypes.LBRACE, TSTypes.RBRACE, true),
        )
    }
}