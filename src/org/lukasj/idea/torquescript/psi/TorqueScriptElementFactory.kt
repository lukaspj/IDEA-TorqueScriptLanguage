package org.lukasj.idea.torquescript.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import org.lukasj.idea.torquescript.TorqueScriptFileType

object TorqueScriptElementFactory {
    fun createFile(project: Project, text: String): TorqueScriptFile {
        return PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.simple", TorqueScriptFileType.INSTANCE, text) as TorqueScriptFile
    }

    fun createTorqueScriptFunctionStatement(project: Project, name: String): TorqueScriptFunctionStatement {
        return createFile(
            project,
            "function ${name}() {}"
        ).firstChild as TorqueScriptFunctionStatement
    }
}