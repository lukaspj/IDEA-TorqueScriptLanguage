package org.lukasj.idea.torquescript.engine.docstring.elements

interface IDocElement {
    val isLeaf: Boolean
    var children: List<IDocElement>
}