package org.lukasj.idea.torquescript.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.TslLanguage

class TslTokenType(debugName: @NonNls String) : IElementType(debugName, TslLanguage.INSTANCE) {
    override fun toString() = "TslTokenType." + super.toString()
}