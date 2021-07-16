package org.lukasj.idea.torquescript.reference

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl
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


    private val OBJECTS = TSObjectCachedListGenerator()
    private val FUNCTIONS = TSFunctionCachedListGenerator()
    private val GLOBALS = TSGlobalVarCachedListGenerator()
}