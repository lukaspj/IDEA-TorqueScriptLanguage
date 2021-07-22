package org.lukasj.idea.torquescript.engine.docstring.elements

abstract class LeafDocElement : IDocElement {
    override val isLeaf = true
    override var children = listOf<IDocElement>()
}