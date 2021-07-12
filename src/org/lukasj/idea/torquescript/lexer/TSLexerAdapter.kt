package org.lukasj.idea.torquescript.lexer

import com.intellij.lexer.FlexAdapter

class TSLexerAdapter : FlexAdapter(TSLexer(null)) {
}