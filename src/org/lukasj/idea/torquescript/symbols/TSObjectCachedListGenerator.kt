package org.lukasj.idea.torquescript.symbols

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.lukasj.idea.torquescript.TSFileType
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.psi.TSObjectDeclaration

class TSObjectCachedListGenerator : TSCachedListGenerator<TSObjectDeclaration>() {
    override fun generate(project: Project): Collection<TSObjectDeclaration> {
        //Search every file in the project
        val virtualFiles = FileTypeIndex.getFiles(
            TSFileType.INSTANCE, GlobalSearchScope.allScope(
                project
            )
        )

        return virtualFiles
            .map { PsiManager.getInstance(project).findFile(it) }
            .filterIsInstance<TSFile>()
            .flatMap(TSFile::getObjects)
            .toSet()
    }
}