package org.lukasj.idea.torquescript.psi

import com.intellij.psi.util.PsiTreeUtil

interface TSObjectDeclaration : TSNamedElement {
    fun getObjectName(): TSObjectName
    fun getParentBlock(): TSParentBlock?
    open fun getTypeName(): String =
        PsiTreeUtil.findSiblingForward(this.firstChild, TSTypes.IDENT, null)!!.text
}