package org.lukasj.idea.torquescript.reference

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionType
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl
import org.lukasj.idea.torquescript.symbols.TSCachedList
import org.lukasj.idea.torquescript.symbols.TSFunctionCachedListGenerator
import org.lukasj.idea.torquescript.symbols.TSGlobalVarCachedListGenerator
import org.lukasj.idea.torquescript.symbols.TSObjectCachedListGenerator


object ReferenceUtil {
    fun getObjects(context: PsiElement, project: Project): Collection<TSObjectDeclaration> =
        CachedValuesManager.getCachedValue(context) {
            val objects = OBJECTS.generate(project)
            CachedValueProvider.Result(
                objects,
                context,
                *objects.toTypedArray()
            )
        }

    fun findObject(context: PsiElement, project: Project, key: String): List<TSObjectDeclaration> =
        CachedValuesManager.getCachedValue(context) {
            val objects =
                getObjects(context, project)
                    .filter { it.name.equals(key, true) }
            CachedValueProvider.Result(
                objects,
                context,
                *objects.toTypedArray()
            )
        }

    fun getFunctions(context: PsiElement, project: Project): Collection<TSFunctionStatementElementImpl> =
        CachedValuesManager.getCachedValue(context) {
            val functions = FUNCTIONS.generate(project)
            CachedValueProvider.Result(
                functions,
                context,
                *functions.toTypedArray()
            )
        }

    fun findFunction(context: PsiElement, project: Project, key: String): List<TSFunctionStatementElementImpl> =
        CachedValuesManager.getCachedValue(context) {
            val functions =
                getFunctions(context, project)
                    .filter {
                        if (it.getFunctionType() != TSFunctionType.GLOBAL)
                            "${it.getNamespace()}::${it.name}".equals(key, true)
                        else
                            it.name.equals(key, true)
                    }
            CachedValueProvider.Result(
                functions,
                context,
                *functions.toTypedArray()
            )
        }

    fun getGlobals(context: PsiElement, project: Project): Collection<TSVarExpressionElementImpl> =
        CachedValuesManager.getCachedValue(context) {
            val globals = GLOBALS.generate(project)
            CachedValueProvider.Result(
                globals,
                context,
                *globals.toTypedArray()
            )
        }

    fun findGlobal(context: PsiElement, project: Project, key: String): List<TSVarExpressionElementImpl> =
        CachedValuesManager.getCachedValue(context) {
            val globals = getGlobals(context, project)
                .filter { it.name.equals(key, true) }
            CachedValueProvider.Result(
                globals,
                context,
                *globals.toTypedArray()
            )
        }


    private val OBJECTS = TSObjectCachedListGenerator()
    private val FUNCTIONS = TSFunctionCachedListGenerator()
    private val GLOBALS = TSGlobalVarCachedListGenerator()
}