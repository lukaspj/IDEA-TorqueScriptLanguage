package org.lukasj.idea.torquescript.psi

interface TSObjectDeclaration : TSNamedElement {
    fun getObjectName(): TSObjectName
    fun getParentBlock(): TSParentBlock
}