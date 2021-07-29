package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import org.lukasj.idea.torquescript.psi.TSElementFactory
import org.lukasj.idea.torquescript.psi.TSNamedElement
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.reference.TSObjectReference

abstract class TSParentBlockElementImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), TSNamedElement {
    override fun getReferences() =
        arrayOf(reference)

    override fun getReference(): PsiReference = TSObjectReference(this, lastChild.textRangeInParent)

    override fun getName(): String = nameIdentifier.text

    override fun setName(name: String): PsiElement {
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

    override fun getNameIdentifier(): PsiElement =
        lastChild
}