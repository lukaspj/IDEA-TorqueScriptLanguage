package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.lukasj.idea.torquescript.psi.*

abstract class TSDatablockDeclarationElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TSObjectDeclaration {

    override fun getNameIdentifier(): PsiElement? =
        getObjectName()

    override fun getTextOffset(): Int {
        val ident = nameIdentifier
        return ident?.textOffset ?: 0
    }

    override fun getName(): String? =
        nameIdentifier?.text

    override fun setName(name: String): PsiElement {
        TODO("Not yet implemented")
    }
}