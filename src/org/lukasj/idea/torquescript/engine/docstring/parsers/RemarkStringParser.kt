package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.RemarkDocElement
import org.lukasj.idea.torquescript.engine.docstring.elements.TextDocElement

class RemarkStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) = tag.tag == "@note"

    override fun parse(tag: DocTag) =
        RemarkDocElement()
            .also { it.children = listOf(TextDocElement(tag.text)) }
}