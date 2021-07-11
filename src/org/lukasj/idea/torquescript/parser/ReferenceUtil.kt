package org.lukasj.idea.torquescript.parser

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.lukasj.idea.torquescript.TorqueScriptFileType
import org.lukasj.idea.torquescript.psi.TorqueScriptBinaryExpression
import org.lukasj.idea.torquescript.psi.TorqueScriptFile
import org.lukasj.idea.torquescript.psi.TorqueScriptFunctionStatement
import org.lukasj.idea.torquescript.psi.TorqueScriptTypes
import org.lukasj.idea.torquescript.psi.impl.TorqueScriptFunctionStatementImpl
import java.util.*

object ReferenceUtil {
    fun findFunctions(project: Project): MutableList<TorqueScriptFunctionStatementImpl> {
        val result = mutableListOf<TorqueScriptFunctionStatementImpl>()
        val virtualFiles = FileTypeIndex.getFiles(
            TorqueScriptFileType.INSTANCE, GlobalSearchScope.allScope(
                project
            )
        )
        for (virtualFile in virtualFiles) {
            val tsFile = PsiManager.getInstance(project).findFile(virtualFile!!) as TorqueScriptFile?
            if (tsFile != null) {
                try {
                    val functions = PsiTreeUtil.findChildrenOfType(
                        tsFile,
                        TorqueScriptFunctionStatementImpl::class.java
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