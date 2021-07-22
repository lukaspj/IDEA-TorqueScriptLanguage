package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.InGroupDocElement

class InGroupStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) = tag.tag == "@ingroup"

    override fun parse(tag: DocTag) = InGroupDocElement(tag.text)
}