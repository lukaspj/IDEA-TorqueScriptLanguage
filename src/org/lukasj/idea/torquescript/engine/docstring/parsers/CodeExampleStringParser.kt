package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.CodeExampleDocElement
import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.TextDocElement

class CodeExampleStringParser : IDocstringParser {
    override fun matchesTag(tag: DocTag) = tag.tag == "@tsexample"

    override fun parse(tag: DocTag) =
        CodeExampleDocElement("TorqueScript")
            .also { it.children = listOf(TextDocElement(tag.text.trim())) }
}