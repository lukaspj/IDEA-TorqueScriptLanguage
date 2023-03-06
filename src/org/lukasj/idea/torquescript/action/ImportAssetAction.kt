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
        ImportAsset().update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        ImportAsset().actionPerformed(e)
    }
}