package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.ParameterRefDocElement

class ParameterRefStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) = tag.tag == "@a"

    override fun parse(tag: DocTag) =
        tag.text.trimStart()
            .let {
                ParameterRefDocElement(
                    it.substring(0, it.indexOf(' ') + 1),
                    it.substring(it.indexOf(' ') + 1)
                )
            }
}