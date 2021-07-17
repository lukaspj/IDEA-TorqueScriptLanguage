package org.lukasj.idea.torquescript.psi.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.SingleRootFileViewProvider
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.psi.impl.source.tree.FileElement
import com.intellij.testFramework.LightVirtualFile
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.psi.TSFile

class TSExprCodeFragmentImpl(project: Project, name: String, text: CharSequence, var physical: Boolean) : TSFile(
    PsiManagerEx.getInstanceEx(project)
        .fileManager
        .createFileViewProvider(
            LightVirtualFile(name, TSLanguage.INSTANCE, text),
            physical
        )
) {
    init {
        (viewProvider as SingleRootFileViewProvider).forceCachedPsi(this)
    }

    private var myViewProvider: FileViewProvider? = null
    private var myContext: PsiElement? = null

    fun setContext(context: PsiElement?) {
        myContext = context
    }

    override fun getContext(): PsiElement? {
        val mc = myContext
        if (mc != null && mc.isValid)
            return mc
        return super.getContext()
    }

    override fun clone(): TSExprCodeFragmentImpl {
        val clone = cloneImpl(calcTreeElement().clone() as FileElement) as TSExprCodeFragmentImpl
        copyCopyableDataTo(clone)
        clone.physical = false
        clone.myOriginalFile = this
        val fileMgr = (manager as PsiManagerEx).fileManager
        val cloneViewProvider = fileMgr.createFileViewProvider(LightVirtualFile(name, language, text), false) as SingleRootFileViewProvider
        cloneViewProvider.forceCachedPsi(clone)
        clone.myViewProvider = cloneViewProvider
        return clone
    }

    override fun isPhysical(): Boolean {
        return physical
    }

    override fun getViewProvider(): FileViewProvider {
        return myViewProvider ?: super.getViewProvider()
    }
}