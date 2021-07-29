package org.lukasj.idea.torquescript.reference

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.elementsAtOffsetUp
import com.intellij.psi.util.parentOfTypes
import org.jetbrains.annotations.Nullable
import org.lukasj.idea.torquescript.completion.TSMethodCallCompletionContributor
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.editor.TSVarExpressionElementImpl
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.TSTypes
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


    fun getNamespaces(rootNs: String, project: Project): List<String> {
        val obj = findObject(project, rootNs)

        if (obj.size > 1) {
            logger<TSMethodCallCompletionContributor>()
                .warn("Too many instances of obj $rootNs")
        }
        if (obj.isEmpty()) {
            val superType = project.getService(EngineApiService::class.java)
                .findClass(rootNs)
                ?.superType
            return if (superType != null) {
                getNamespaces(superType, project)
                    .plus(rootNs)
            } else {
                logger<TSMethodCallCompletionContributor>()
                    .warn("No instances of $rootNs found")
                listOf(rootNs)
            }
        }

        return if (obj[0].getParentBlock() != null) {
            getNamespaces(obj[0].getParentBlock()!!.lastChild.text, project)
        } else {
            getNamespaces(obj[0].getTypeName(), project)
        }.plus(rootNs)
    }

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