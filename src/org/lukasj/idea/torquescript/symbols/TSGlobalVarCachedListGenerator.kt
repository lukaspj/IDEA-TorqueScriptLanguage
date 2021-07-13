package org.lukasj.idea.torquescript.symbols

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.TSFileType
import org.lukasj.idea.torquescript.psi.TSAssignmentExpression
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.impl.TSVarExpressionElementImpl

class TSGlobalVarCachedListGenerator : TSCachedListGenerator<TSVarExpressionElementImpl>() {
    override fun generate(project: Project): Set<TSVarExpressionElementImpl> {
        val items: MutableSet<TSVarExpressionElementImpl> = HashSet()
        //Search every file in the project
        val virtualFiles = FileTypeIndex.getFiles(
            TSFileType.INSTANCE, GlobalSearchScope.allScope(
                project
            )
        )
        for (virtualFile in virtualFiles) {
            val tsFile = PsiManager.getInstance(project).findFile(virtualFile!!)
            if (tsFile != null && tsFile is TSFile) {
                val assignments = PsiTreeUtil.findChildrenOfType(tsFile, TSAssignmentExpression::class.java)
                for (assignment in assignments) {
                    if (assignment.accessorChain != null) {
                        continue
                    }

                    val first = assignment.firstChild
                    if (first !is TSVarExpressionElementImpl
                        || first.firstChild.elementType != TSTypes.GLOBALVAR
                    ) {
                        continue
                    }

                    items.add(first)
                }
            }
            ProgressManager.progress("Loading symbols")
        }
        return items
    }
}