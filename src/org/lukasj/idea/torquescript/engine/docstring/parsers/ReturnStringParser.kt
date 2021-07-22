package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.ReturnDocElement
import org.lukasj.idea.torquescript.engine.docstring.elements.TextDocElement

class ReturnStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) =
        tag.tag == "@return"
                || tag.tag == "@returns"

    override fun parse(tag: DocTag) =
        ReturnDocElement()
            .also { it.children = listOf(TextDocElement(tag.text)) }
}