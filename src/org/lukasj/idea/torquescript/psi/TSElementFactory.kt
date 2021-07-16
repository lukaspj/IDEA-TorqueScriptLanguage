package org.lukasj.idea.torquescript.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import org.lukasj.idea.torquescript.TSFileType

object TSElementFactory {
    fun createFile(project: Project, text: String): TSFile {
        return PsiFileFactory.getInstance(project)
            .createFileFromText("dummy.simple", TSFileType.INSTANCE, text) as TSFile
    }

    fun createTorqueScriptFunctionStatement(project: Project, name: String): TSFunctionDeclaration {
        return createFile(
            project,
            "function ${name}() {}"
        ).firstChild as TSFunctionDeclaration
    }

    fun createDatablockStatement(project: Project, name: String): TSDatablockStatement {
        return createFile(
            project,
            "datablock ${name}() {};"
        ).firstChild.firstChild.firstChild as TSDatablockStatement
    }

    fun createIdent(project: Project, name: String): TSIdentExpression = createFile(
        project,
        "${name};"
    ).firstChild!!.firstChild!!.firstChild!!.firstChild as TSIdentExpression
}