package org.lukasj.idea.torquescript.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import org.lukasj.idea.torquescript.TSFileType

object TSElementFactory {
    fun createFile(project: Project, text: String): TSFile {
        return PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.simple", TSFileType.INSTANCE, text) as TSFile
    }

    fun createTorqueScriptFunctionStatement(project: Project, name: String): TSFunctionStatement {
        return createFile(
            project,
            "function ${name}() {}"
        ).firstChild as TSFunctionStatement
    }
}