package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.IDocElement
import org.lukasj.idea.torquescript.engine.docstring.elements.SeeDocElement

class SeeStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) = tag.tag == "@see"

    override fun parse(tag: DocTag) = SeeDocElement(tag.text.trim())
}