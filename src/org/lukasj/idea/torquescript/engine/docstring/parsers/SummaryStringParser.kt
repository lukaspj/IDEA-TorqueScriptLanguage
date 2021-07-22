package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.*

class SummaryStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) = tag.tag == "@brief"

    override fun parse(tag: DocTag) =
        if (tag.text.contains('\n')) {
            CompoundDocElement()
                .also { element ->
                    element.children = listOf(
                        SummaryDocElement()
                            .also { it.children = listOf(TextDocElement(tag.text.substring(0, tag.text.indexOf('\n')).trim())) },
                        DescriptionDocElement()
                            .also { it.children = listOf(TextDocElement(tag.text.substring(tag.text.indexOf('\n')).trim())) }
                    )
                }
        } else {
            SummaryDocElement()
                .also { it.children = listOf(TextDocElement(tag.text.trim())) }
        }
}