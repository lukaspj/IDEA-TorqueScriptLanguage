package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.NullDocElement

class NullStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) =
        tag.tag.isEmpty() && tag.text.isEmpty()
                || tag.tag.startsWith("(")
                || tag.tag.startsWith("@")
                || tag.tag.startsWith("@endtsexample")
                || tag.tag.startsWith("@hide")

    override fun parse(tag: DocTag) =
        NullDocElement(tag.tag, tag.text)
}