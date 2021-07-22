package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.ParameterDocElement
import org.lukasj.idea.torquescript.engine.docstring.elements.TextDocElement

class ParameterStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) = tag.tag == "@param"
            || tag.tag == "@params"

    override fun parse(tag: DocTag) =
        tag.text.trim()
            .let { text ->
                if (text.indexOf(' ') > -1) {
                    ParameterDocElement(
                        text.substring(0, text.indexOf(' ')),
                    ).also { it.children = listOf(TextDocElement(text.substring(text.indexOf(' ')).trim())) }
                } else {
                    ParameterDocElement(text)
                }
            }
}