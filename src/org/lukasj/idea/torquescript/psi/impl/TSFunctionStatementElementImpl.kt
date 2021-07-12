package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import org.lukasj.idea.torquescript.psi.TSFunctionIdentifier
import org.lukasj.idea.torquescript.psi.TSNamedElement
import org.lukasj.idea.torquescript.psi.TSParams
import org.lukasj.idea.torquescript.psi.TorqueScriptTypes

abstract class TSFunctionStatementElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TSNamedElement {
    abstract fun getFunctionIdentifier(): TSFunctionIdentifier
    abstract fun getParams(): TSParams

    override fun getNameIdentifier(): PsiElement? =
        getFunctionIdentifier().lastChild

    override fun getName(): String? =
        nameIdentifier!!.text

    override fun setName(name: String): PsiElement {
        TODO("Not yet implemented")
    }

    fun getParameters(): List<PsiElement> {
        val child = getParams().firstChild
        if (child == null) {
            return listOf()
        }
        if (child.nextSibling == null) {
            return listOf(child)
        }
        return child.siblings().toList()
            .filter {
                it.elementType == TorqueScriptTypes.LOCALVAR
                        || it.elementType == TorqueScriptTypes.THISVAR
            }
    }
}

