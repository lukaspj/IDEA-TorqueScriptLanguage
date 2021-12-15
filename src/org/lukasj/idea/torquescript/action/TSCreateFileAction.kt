package org.lukasj.idea.torquescript.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import icons.TSIcons

class TSCreateFileAction : CreateFileFromTemplateAction(CAPTION, "", TSIcons.FILE) {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle(CAPTION)
            .addKind("File", TSIcons.FILE, TS_FILE_KIND)
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String =
        CAPTION

    override fun createFile(name: String?, templateName: String?, dir: PsiDirectory?): PsiFile? {
        if (name == null) return null

        val cleanedName = name.removeSuffix(".tscript")
        if (templateName == TS_FILE_KIND) {
            return super.createFile(cleanedName, templateName, dir)
        }
        return null
    }

    companion object {
        val CAPTION = "TorqueScript File"
        val TS_MODULE_KIND = "TorqueScript Module"
        val TS_FILE_KIND = "TorqueScript File"
    }
}