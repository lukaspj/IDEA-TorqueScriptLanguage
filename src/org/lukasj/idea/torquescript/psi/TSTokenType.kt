package org.lukasj.idea.torquescript.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.lukasj.idea.torquescript.TSLanguage

class TSTokenType(debugName: String) : IElementType(debugName, TSLanguage.INSTANCE) {
    override fun toString() = "TSTokenType." + super.toString()
}