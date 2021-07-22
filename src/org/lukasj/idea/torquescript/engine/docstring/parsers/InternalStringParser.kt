package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.InternalDocElement

class InternalStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) = tag.tag == "@internal"

    override fun parse(tag: DocTag) = InternalDocElement()
}