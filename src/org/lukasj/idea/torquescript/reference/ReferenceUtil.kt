package org.lukasj.idea.torquescript.reference

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parentOfTypes
import org.lukasj.idea.torquescript.psi.TSElementType
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.symbols.TSFunctionCachedListGenerator
import org.lukasj.idea.torquescript.symbols.TSGlobalVarCachedListGenerator
import org.lukasj.idea.torquescript.symbols.TSObjectCachedListGenerator


object ReferenceUtil {
    fun getObjects(project: Project): Collection<TSObjectDeclaration> =
        OBJECTS.generate(project)

    fun findObject(project: Project, key: String): List<TSObjectDeclaration> =
        getObjects(project)
            .filter { it.name.equals(key, true) }

    fun getFunctions(project: Project): Collection<TSFunctionStatementElementImpl> =
        FUNCTIONS.generate(project)

    fun findFunction(project: Project, key: String): List<TSFunctionStatementElementImpl> =
        getFunctions(project)
            .filter {
                if (it.getFunctionType() != TSFunctionType.GLOBAL)
                    "${it.getNamespace()}::${it.name}".equals(key, true)
                else
                    it.name.equals(key, true)
            }

    fun getGlobals(project: Project): Collection<TSVarExpressionElementImpl> =
        GLOBALS.generate(project)

    fun findGlobal(project: Project, key: String): List<TSVarExpressionElementImpl> =
        getGlobals(project)
            .filter { it.name.equals(key, true) }

    private fun findPreviousElement(element: PsiElement, skipSet: TokenSet): PsiElement? {
        var leaf = PsiTreeUtil.prevLeaf(element)
        while (leaf != null && skipSet.contains(leaf.elementType)) {
            leaf = PsiTreeUtil.prevLeaf(leaf)
        }
        return leaf
    }

    fun tryResolveType(element: PsiElement): String? {
        if (element.node.elementType == TSTypes.IDENT)
            return element.text

        return when (element.elementType) {
            TSTypes.IDENT -> element.text
            TSTypes.IDENT_EXPRESSION -> element.text
            TSTypes.LOCALVAR ->
                findPreviousElement(element, TokenSet.create(TokenType.WHITE_SPACE, TokenType.ERROR_ELEMENT))
                    .let { prevElement ->
                        if (prevElement != null) {
                            findLocalVariablesForContext(prevElement)
                                .filterNot { it.textRange.contains(element.textRange) }
                                .filter { it.text.equals(element.text, true) }
                                .map { tryResolveType(it) }
                                .firstOrNull()
                        } else {
                            null
                        }
                    }
            TSTypes.GLOBALVAR ->
                findGlobal(element.project, element.text)
                    .mapNotNull { tryResolveType(it) }
                    .firstOrNull()
            TSTypes.THISVAR ->
                element.parentOfTypes(TSFunctionStatementElementImpl::class)?.getNamespace()
            TSTypes.THIS_VAR_EXPRESSION ->
                tryResolveType(element.firstChild)
            TSTypes.VAR_EXPRESSION ->
                when (element.parent.elementType) {
                    TSTypes.ASSIGNMENT_EXPRESSION ->
                        if (element.parent.firstChild == element) {
                            tryResolveType(element.parent.lastChild)
                        } else {
                            if (element.reference != null) {
                                element.reference!!.resolve()
                                    .let {
                                        if (it != null) {
                                            tryResolveType(it)
                                        } else {
                                            tryResolveType(element.parent)
                                        }
                                    }
                            } else {
                                null
                            }
                        }
                    TSTypes.PARAMS ->
                        null
                    else ->
                        if (element.reference != null) {
                            element.reference!!.resolve()
                                .let {
                                    if (it != null) {
                                        tryResolveType(it)
                                    } else {
                                        tryResolveType(element.parent)
                                    }
                                }
                        } else if (element.firstChild.elementType == TSTypes.THISVAR) {
                            tryResolveType(element.firstChild)
                        } else {
                            null
                        }
                }
            TSTypes.DATABLOCK_STATEMENT,
            TSTypes.SINGLETON_STATEMENT,
            TSTypes.NEW_INSTANCE_EXPRESSION ->
                (element as TSObjectDeclaration)
                    .let {
                        it.getParentBlock()?.lastChild?.text
                            ?: it.getTypeName()
                    }
            else -> null
        }
    }

    fun findLocalVariablesForContext(element: PsiElement): List<TSVarExpressionElementImpl> =
        element.parentOfTypes(TSFunctionStatementElementImpl::class)
            .let { functionParent ->
                if (functionParent != null) {
                    PsiTreeUtil.findChildrenOfType(functionParent, TSVarExpressionElementImpl::class.java)
                        .plus(functionParent.getParameters()
                            .filter { it.elementType == TSTypes.LOCALVAR || it.elementType == TSTypes.THISVAR })
                } else {
                    // Assume file-scoped
                    (element.containingFile as TSFile).getVariables()
                        ?: listOf()
                }
            }.map { it as TSVarExpressionElementImpl }


    private val OBJECTS = TSObjectCachedListGenerator()
    private val FUNCTIONS = TSFunctionCachedListGenerator()
    private val GLOBALS = TSGlobalVarCachedListGenerator()
}