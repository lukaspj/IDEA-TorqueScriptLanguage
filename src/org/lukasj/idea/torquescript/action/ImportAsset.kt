package org.lukasj.idea.torquescript.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.lukasj.idea.torquescript.asset.AssetImporter

class ImportAsset : ImportAssetHandler {
    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val project = e.project
        val haveSmthToDo: Boolean
        if (project == null || file == null || file.isDirectory) {
            haveSmthToDo = false
        } else {
            // the action should also be available for files which have been auto-detected as text or as a particular language (IDEA-79574)
            haveSmthToDo = AssetImporter().accepts(file)
        }
        presentation.isVisible = haveSmthToDo
        presentation.isEnabled = haveSmthToDo
    }

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE)
        AssetImporter().import(e.project, file)
    }
}