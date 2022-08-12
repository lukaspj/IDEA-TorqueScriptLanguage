package org.lukasj.idea.torquescript.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import kotlin.reflect.full.createInstance

interface ImportAssetHandler {
    fun update(e: AnActionEvent)
    fun actionPerformed(e: AnActionEvent)
}

class ImportAssetAction : AnAction() {
    override fun update(e: AnActionEvent) {
        // Hacky-workaround for legacy code
        (try {
            Class.forName("org.lukasj.idea.torquescript.action.LegacyImportAsset")
        } catch (ex: ClassNotFoundException) {
            Class.forName("org.lukasj.idea.torquescript.action.ImportAsset")
        }.kotlin.createInstance() as ImportAssetHandler).update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        // Hacky-workaround for legacy code
        (try {
            Class.forName("org.lukasj.idea.torquescript.action.LegacyImportAsset")
        } catch (ex: ClassNotFoundException) {
            Class.forName("org.lukasj.idea.torquescript.action.ImportAsset")
        }.kotlin.createInstance() as ImportAssetHandler).actionPerformed(e)
    }
}