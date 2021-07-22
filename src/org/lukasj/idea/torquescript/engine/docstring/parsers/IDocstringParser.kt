package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.DocTag
import org.lukasj.idea.torquescript.engine.docstring.elements.IDocElement

interface IDocstringParser {
    fun matchesTag(tag: DocTag): Boolean
    fun parse(tag: DocTag): IDocElement
}