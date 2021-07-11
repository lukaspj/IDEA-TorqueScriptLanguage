package org.lukasj.idea.torquescript.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.lukasj.idea.torquescript.TorqueScriptLanguage

class TorqueScriptTokenType(debugName: @NonNls String) : IElementType(debugName, TorqueScriptLanguage.INSTANCE) {
    override fun toString() = "TorqueScriptTokenType." + super.toString()
}