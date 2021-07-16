package org.lukasj.idea.torquescript.action

import com.intellij.execution.RunManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import org.lukasj.idea.torquescript.runner.TSRunConfiguration

class RebuildExportsAction : AnAction() {
    override fun isDefaultIcon(): Boolean {
        return super.isDefaultIcon()
    }

    override fun update(e: AnActionEvent) {
        val project = e.project

        if (project == null) {
            e.presentation.isEnabledAndVisible = e.project != null
        } else {
            e.presentation.isVisible = true
            e.presentation.isEnabled =
                RunManager.getInstance(project)
                    .allConfigurationsList
                    .filterIsInstance<TSRunConfiguration>()
                    .filter { !it.appPath.isNullOrEmpty() }
                    .any()
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val dlgMsg = StringBuffer(e.presentation.text + "Selected")
        val dlgTitle = e.presentation.description

        val nav = e.getData(CommonDataKeys.NAVIGATABLE)
        if (nav != null) {
            dlgMsg.append("\nSelected Element: $nav")
        }

        Messages.showMessageDialog(project, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon())
    }
}