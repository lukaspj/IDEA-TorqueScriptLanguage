package org.lukasj.idea.torquescript.engine.docstring.elements

abstract class DocElement : IDocElement {
    override val isLeaf = false
    override var children = listOf<IDocElement>()
}