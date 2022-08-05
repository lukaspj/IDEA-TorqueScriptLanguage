package org.lukasj.idea.torquescript.action

import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.options.SettingsEditor
import org.lukasj.idea.torquescript.asset.AssetImporter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

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