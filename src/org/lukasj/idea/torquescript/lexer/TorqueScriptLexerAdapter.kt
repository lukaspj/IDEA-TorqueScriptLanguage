package org.lukasj.idea.torquescript.lexer

import com.intellij.lexer.FlexAdapter

class TorqueScriptLexerAdapter : FlexAdapter(TorqueScriptLexer(null)) {
}