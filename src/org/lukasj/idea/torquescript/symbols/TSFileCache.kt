package org.lukasj.idea.torquescript.symbols

import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.util.*
import org.lukasj.idea.torquescript.psi.TSAssignmentExpression
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.TSFunctionDeclaration
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration
import org.lukasj.idea.torquescript.psi.TSPackageDeclaration
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl

class TSModificationTracker : ModificationTracker {
    var count = 0L
    override fun getModificationCount() = count
}

object TSFileCacheGenerator {
    fun getFunctions(file: TSFile) =
        (PsiTreeUtil.getChildrenOfType(file, TSFunctionDeclaration::class.java)
            ?.map { it as TSFunctionStatementElementImpl }
            ?: setOf())
            .plus(
                PsiTreeUtil.getChildrenOfType(file, TSPackageDeclaration::class.java)
                    ?.flatMap { it.functionDeclarationList }
                    ?.map { it as TSFunctionStatementElementImpl }
                    ?: setOf()
            )

    fun getObjects(file: TSFile): Collection<TSObjectDeclaration> =
        PsiTreeUtil.findChildrenOfType(file, TSObjectDeclaration::class.java)

    fun getGlobals(file: TSFile) =
        PsiTreeUtil.findChildrenOfType(file, TSAssignmentExpression::class.java)
            .filter { it.accessorChain == null }
            .map { it.firstChild }
            .filter { it is TSVarExpressionElementImpl && it.firstChild.elementType == TSTypes.GLOBALVAR }
            .map { it as TSVarExpressionElementImpl }

}

class TSFileCache(private val file: TSFile) {
    private val modificationTracker = TSModificationTracker()

    private var cachedFunctions: CachedValue<Map<String, TSFunctionStatementElementImpl>>? = null
    private var cachedObjects: CachedValue<Map<String, TSObjectDeclaration>>? = null
    private var cachedGlobals: CachedValue<Map<String, TSVarExpressionElementImpl>>? = null

    init {
        buildCaches()
    }

    fun dropCaches() {
        modificationTracker.count++
    }

    private fun buildCaches() {
        val manager = CachedValuesManager.getManager(file.project)
        val dependencies = arrayOf(file, modificationTracker)

        cachedFunctions =
            manager.createCachedValue(
                {
                    CachedValueProvider.Result.create(
                        TSFileCacheGenerator.getFunctions(file)
                            .filter { it.name != null }
                            .associateBy { it.getFunctionIdentifier()!!.text },
                        dependencies
                    )
                },
                false
            )

        cachedObjects =
            manager.createCachedValue(
                {
                    CachedValueProvider.Result.create(
                        TSFileCacheGenerator.getObjects(file)
                            .filter { it.name != null }
                            .associateBy { it.name!! },
                        dependencies
                    )
                },
                false
            )

        cachedGlobals =
            manager.createCachedValue(
                {
                    CachedValueProvider.Result.create(
                        TSFileCacheGenerator.getGlobals(file)
                            .filter { it.name != null }
                            .associateBy { it.name!! },
                        dependencies
                    )
                },
                false
            )
    }

    fun getFunctions() =
        cachedFunctions!!.value.values

    fun getObjects() =
        cachedObjects!!.value.values

    fun getGlobals() =
        cachedGlobals!!.value.values

}