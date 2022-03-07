package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.layout.panel
import javax.swing.JComponent

class TSRunConfigurationSettingsEditor(project: Project) : SettingsEditor<TSRunConfiguration>() {
    private val appPathComponent = TextFieldWithBrowseButton()
    private val workingDirectoryComponent = TextFieldWithBrowseButton()
    private val mainScriptComponent = TextFieldWithBrowseButton()
    private val mainPanel =
        panel() {
            row("Executable:") { appPathComponent() }
            row("Working Directory:") { workingDirectoryComponent() }
            row("Main Script") { mainScriptComponent() }
        }

    init {
        appPathComponent.addBrowseFolderListener(
            "App Path",
            "Specify path to Torque App executable",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
        workingDirectoryComponent.addBrowseFolderListener(
            "Working Directory",
            "Specify path to the working directory",
            project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
        )
        mainScriptComponent.addBrowseFolderListener(
            "Main Script",
            "Specify path to the main script file",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
    }

    override fun resetEditorFrom(s: TSRunConfiguration) {
        val appPath = s.appPath
        if (appPath != null) {
            appPathComponent.text = appPath
        }
        val workingDirectory = s.workingDirectory
        if (workingDirectory != null) {
            workingDirectoryComponent.text = workingDirectory
        }
        val mainScript = s.mainScript
        if (mainScript != null) {
            mainScriptComponent.text = mainScript
        }
    }

    override fun applyEditorTo(s: TSRunConfiguration) {
        s.appPath = appPathComponent.text.trim()
        s.workingDirectory = workingDirectoryComponent.text.trim()
        s.mainScript = mainScriptComponent.text.trim()
    }

    override fun createEditor(): JComponent =
        mainPanel
}
