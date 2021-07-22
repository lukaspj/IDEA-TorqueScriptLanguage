package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.TextDocElement

class InlineStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) =
        when (tag.tag.trim()) {
            "@" -> true
            "@cell" -> true
            else -> false
        }

    override fun parse(tag: DocTag) =
        TextDocElement(tag.tag + tag.text)
}