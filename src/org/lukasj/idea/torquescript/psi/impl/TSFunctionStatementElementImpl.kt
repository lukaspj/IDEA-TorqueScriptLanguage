package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.siblings
import com.intellij.util.PlatformIcons
import org.lukasj.idea.torquescript.psi.*

abstract class TSFunctionStatementElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TSNamedElement {
    abstract fun getFunctionIdentifier(): TSFunctionIdentifier?
    abstract fun getParams(): TSParams


    override fun getNameIdentifier(): PsiElement? =
        getFunctionIdentifier()?.lastChild

    fun getNamespace(): String? =
        if (getFunctionType() != TSFunctionType.GLOBAL)
            getFunctionIdentifier()?.firstChild?.text
        else
            null

    override fun getName(): String? =
        nameIdentifier?.text

    override fun setName(name: String): PsiElement {
        nameIdentifier?.replace(TSElementFactory.createSimple<TSIdentExpressionImpl>(project, name))
        return this
    }

    fun getFunctionType(): TSFunctionType {
        // The identifier is either a name or namespaced name, so we can check whether it consists of multiple
        // elements to check if it's a global
        if (getFunctionIdentifier()?.firstChild == getFunctionIdentifier()?.lastChild) {
            return TSFunctionType.GLOBAL
        }

        val parameters = getParameters()
        if (parameters.isNullOrEmpty()) {
            // Generally, if there is no parameters, then it is not a method because it doesn't have a %this parameter
            return TSFunctionType.GLOBAL_NS
        }

        // Make an assumption that first parameter for methods should be %this,
        // otherwise it's probably a function not a method
        if (parameters.first().elementType == TSTypes.THISVAR) {
            return TSFunctionType.METHOD
        }

        // Probably global namespace
        return TSFunctionType.GLOBAL_NS
    }

    fun getParameters(): List<PsiElement> {
        val child = getParams().firstChild ?: return listOf()
        if (child.nextSibling == null) {
            return listOf(child)
        }
        return child.siblings().toList()
            .filter {
                it.elementType == TSTypes.LOCALVAR
                        || it.elementType == TSTypes.THISVAR
            }
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: 0

    override fun getPresentation(): ItemPresentation? {
        return object : ItemPresentation {
            override fun getPresentableText() =
                if (getNamespace() != null) {
                    "${getNamespace()}::${name}"
                } else {
                    name!!
                }

            override fun getLocationString() = containingFile.name

            override fun getIcon(unused: Boolean) =
                PlatformIcons.FUNCTION_ICON
        }
    }
}

