package org.lukasj.idea.torquescript.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.PsiFileImpl
import com.intellij.psi.util.PsiTreeUtil
import org.lukasj.idea.torquescript.TSFileType
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl
import org.lukasj.idea.torquescript.symbols.TSFileCache

open class TSFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TSLanguage.INSTANCE) {
    private val bodyCache by lazy { TSFileCache(this) }

    override fun getFileType(): FileType {
        return TSFileType.INSTANCE
    }

    override fun toString(): String {
        return "TorqueScript File"
    }

    override fun subtreeChanged() {
        dropCaches()
        super.subtreeChanged()
    }

    private fun dropCaches() =
        bodyCache.dropCaches()

    override fun clone(): PsiFileImpl {
        val clone = super.clone() as TSFile
        clone.dropCaches()
        return clone
    }

    fun getFunctions() = bodyCache.getFunctions()
    fun getObjects() = bodyCache.getObjects()
    fun getGlobals() = bodyCache.getGlobals()

    fun getVariables() =
        PsiTreeUtil.getChildrenOfType(this, TSStatement::class.java)
            ?.flatMap {
                PsiTreeUtil.findChildrenOfAnyType(it, TSVarExpressionElementImpl::class.java)
            }

    fun getEnclosingFunction(element: PsiElement): TSFunctionStatementElementImpl? =
        PsiTreeUtil.findFirstParent(element) {
            it is TSFunctionStatementElementImpl
        } as TSFunctionStatementElementImpl?
}