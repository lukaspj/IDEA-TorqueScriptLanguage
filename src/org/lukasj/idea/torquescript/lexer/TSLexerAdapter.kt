package org.lukasj.idea.torquescript.lexer

import com.intellij.lexer.FlexAdapter

class TSLexerAdapter : FlexAdapter(TorqueScriptLexer(null)) {
}