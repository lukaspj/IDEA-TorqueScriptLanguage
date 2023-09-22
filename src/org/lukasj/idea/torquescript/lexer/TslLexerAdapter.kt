package org.lukasj.idea.torquescript.lexer

import com.intellij.lexer.FlexAdapter

class TslLexerAdapter : FlexAdapter(TslLexer(null)) {
}