package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.lukasj.idea.torquescript.psi.*

abstract class TSObjectDeclarationElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
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

        val nameIdentifier = getObjectName().firstChild
        val nameNode: ASTNode? = nameIdentifier.firstChild.node

        if (nameNode != null) {
            val stmt = TSElementFactory.createSimple<TSIdentExpressionImpl>(project, name)
            val newNameNode: ASTNode? = stmt.node.findChildByType(TSTypes.IDENT)
            if (newNameNode != null) {
                nameIdentifier.node.replaceChild(nameNode, newNameNode)
            }
        }

        return this
    }
}