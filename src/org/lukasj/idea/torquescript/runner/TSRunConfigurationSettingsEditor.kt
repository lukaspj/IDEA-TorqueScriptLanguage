package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JComponent

class TSRunConfigurationSettingsEditor(project: Project) : SettingsEditor<TSRunConfiguration>() {
    private val model = TSRunConfigurationEditorModel()
    private val mainPanel =
        panel {
            row("Executable:") {
                textFieldWithBrowseButton (
                    "App Path",
                        project,
                        FileChooserDescriptorFactory.createSingleFileDescriptor()
                    )
                    .bindText(model::appPath)
                    .comment("Specify path to Torque App executable")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
            row("Working Directory:") {
                textFieldWithBrowseButton (
                    "Working Directory",
                        project,
                        FileChooserDescriptorFactory.createSingleFileDescriptor()
                    )
                    .bindText(model::workingDirectory)
                    .comment("Specify path to the working directory")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
            row("Main Script:") {
                textFieldWithBrowseButton (
                    "Main Script",
                        project,
                        FileChooserDescriptorFactory.createSingleFileDescriptor()
                    )
                    .bindText(model::mainScript)
                    .comment("Specify path to the main script file")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
        }

    override fun resetEditorFrom(s: TSRunConfiguration) {
        val appPath = s.appPath
        if (appPath != null) {
            model.appPath = appPath
        }
        model.workingDirectory = s.workingDirectory
        model.mainScript = s.mainScript
        mainPanel.reset()
    }

    override fun applyEditorTo(s: TSRunConfiguration) {
        s.appPath = model.appPath.trim()
        s.workingDirectory = model.workingDirectory.trim()
        s.mainScript = model.mainScript.trim()
    }

    override fun createEditor(): JComponent =
        mainPanel
}

internal data class TSRunConfigurationEditorModel(
    var appPath: String = "",
    var workingDirectory: String = "",
    var mainScript: String = "",
)