package org.lukasj.idea.torquescript.psi

import com.intellij.psi.util.PsiTreeUtil

interface TSObjectDeclaration : TSNamedElement {
    fun getObjectTypeName(): TSObjectTypeName
    fun getObjectName(): TSObjectName
    fun getParentBlock(): TSParentBlock?
}