package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import org.lukasj.idea.torquescript.psi.TorqueScriptFunctionIdentifier
import org.lukasj.idea.torquescript.psi.TorqueScriptNamedElement
import org.lukasj.idea.torquescript.psi.TorqueScriptParams
import org.lukasj.idea.torquescript.psi.TorqueScriptTypes

abstract class TorqueScriptFunctionStatementElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TorqueScriptNamedElement {
    abstract fun getFunctionIdentifier(): TorqueScriptFunctionIdentifier
    abstract fun getParams(): TorqueScriptParams

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

