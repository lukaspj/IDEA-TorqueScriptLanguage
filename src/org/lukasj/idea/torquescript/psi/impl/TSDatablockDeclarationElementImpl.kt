package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.lukasj.idea.torquescript.psi.TSDatablockStatement
import org.lukasj.idea.torquescript.psi.TSElementFactory
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.psi.TSTypes


abstract class TSDatablockDeclarationElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TSObjectDeclaration {

    fun getClassName(): String? =
        firstChild.nextSibling.nextSibling.text

    override fun getNameIdentifier(): PsiElement? =
        getObjectName()

    override fun getTextOffset(): Int {
        val ident = nameIdentifier
        return ident?.textOffset ?: 0
    }

    override fun getName(): String? =
        nameIdentifier?.text

    override fun setName(name: String): PsiElement {
        val nameNode: ASTNode? = node.findChildByType(TSTypes.IDENT)

        if (nameNode != null) {
            val stmt: TSDatablockStatement = TSElementFactory.createDatablockStatement(project, name)
            val newNameNode: ASTNode? = stmt.node.findChildByType(TSTypes.IDENT)
            if (newNameNode != null) {
                node.replaceChild(nameNode, newNameNode)
            }
        }

        return this
    }
}