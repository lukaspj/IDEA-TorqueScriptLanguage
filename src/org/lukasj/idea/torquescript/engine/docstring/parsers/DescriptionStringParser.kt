package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DescriptionDocElement
import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.TextDocElement

class DescriptionStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) =
        tag.text.isNotEmpty()
                && (tag.tag.isEmpty() || !tag.tag.startsWith("(") && !tag.tag.startsWith("@"))

    override fun parse(tag: DocTag) =
        DescriptionDocElement()
            .also { it.children = listOf(TextDocElement(tag.text)) }
}