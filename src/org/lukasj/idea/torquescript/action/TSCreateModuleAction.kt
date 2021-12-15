package org.lukasj.idea.torquescript.action

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import icons.TSIcons

class TSCreateModuleAction : CreateFileFromTemplateAction(CAPTION, "", TSIcons.FILE) {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle(CAPTION)
            .addKind("Module", TSIcons.FILE, TS_MODULE_KIND)
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String =
        CAPTION

    override fun createFile(name: String?, templateName: String?, dir: PsiDirectory?): PsiFile? {
        if (name == null) return null

        val cleanedName = name.removeSuffix(".tscript")
        if (templateName == TS_MODULE_KIND) {
            val moduleScript = super.createFile("${cleanedName}/${cleanedName}", TS_MODULE_KIND, dir)
            val moduleTaml = super.createFile("${cleanedName}/${cleanedName}", TS_MODULE_DEFINITION_KIND, dir)
            return moduleScript
        }

        return null
    }

    companion object {
        val CAPTION = "TorqueScript Module"
        val TS_MODULE_KIND = "TorqueScript Module"
        val TS_MODULE_DEFINITION_KIND = "TorqueScript Module TAML"
    }
}