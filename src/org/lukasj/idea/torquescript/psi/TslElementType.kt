package org.lukasj.idea.torquescript.psi

import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls
import org.lukasj.idea.torquescript.TslLanguage

class TslElementType(debugName: @NonNls String) : IElementType(debugName, TslLanguage.INSTANCE) {
}