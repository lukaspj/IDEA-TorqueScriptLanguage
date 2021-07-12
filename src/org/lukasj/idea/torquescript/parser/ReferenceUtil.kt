package org.lukasj.idea.torquescript.parser

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.lukasj.idea.torquescript.TSFileType
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.impl.TSFunctionDeclarationImpl

object ReferenceUtil {
    fun findFunctions(project: Project): MutableList<TSFunctionDeclarationImpl> {
        val result = mutableListOf<TSFunctionDeclarationImpl>()
        val virtualFiles = FileTypeIndex.getFiles(
            TSFileType.INSTANCE, GlobalSearchScope.allScope(
                project
            )
        )
        for (virtualFile in virtualFiles) {
            val tsFile = PsiManager.getInstance(project).findFile(virtualFile!!)
            if (tsFile != null && tsFile is TSFile) {
                try {
                    val functions = PsiTreeUtil.findChildrenOfType(
                        tsFile,
                        TSFunctionDeclarationImpl::class.java
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