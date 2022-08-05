package org.lukasj.idea.torquescript.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class LegacyImportAssetAction : ImportAssetHandler {
    override fun update(e: AnActionEvent) {
        val presentation = e.presentation
        presentation.isVisible = false
        presentation.isEnabled = false
    }

    override fun actionPerformed(e: AnActionEvent) {
        Messages.showMessageDialog(
            e.project,
            "This action is not supported in legacy versions of this plugin",
            "Not Supported",
            Messages.getErrorIcon()
        )
    }
}