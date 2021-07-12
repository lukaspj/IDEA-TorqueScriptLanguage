package org.lukasj.idea.torquescript.parser

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.lukasj.idea.torquescript.TSFileType
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.impl.TSFunctionStatementImpl

object ReferenceUtil {
    fun findFunctions(project: Project): MutableList<TSFunctionStatementImpl> {
        val result = mutableListOf<TSFunctionStatementImpl>()
        val virtualFiles = FileTypeIndex.getFiles(
            TSFileType.INSTANCE, GlobalSearchScope.allScope(
                project
            )
        )
        for (virtualFile in virtualFiles) {
            val tsFile = PsiManager.getInstance(project).findFile(virtualFile!!) as TSFile?
            if (tsFile != null) {
                try {
                    val functions = PsiTreeUtil.findChildrenOfType(
                        tsFile,
                        TSFunctionStatementImpl::class.java
                    )
                    result.addAll(functions)
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}