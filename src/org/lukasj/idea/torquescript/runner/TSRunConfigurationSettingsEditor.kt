package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.layout.panel
import org.apache.http.client.fluent.Form.form
import javax.swing.JComponent
import javax.swing.JPanel

class TSRunConfigurationSettingsEditor(project: Project) : SettingsEditor<TSRunConfiguration>() {
    private val appPathComponent = TextFieldWithBrowseButton()
    private val mainPanel =
        panel() {
            row("Executable:") { appPathComponent() }
        }


    init {
        appPathComponent.addBrowseFolderListener(
            "App Path",
            "Specify path to Torque App executable",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )
    }

    override fun resetEditorFrom(s: TSRunConfiguration) {
        val appPath = s.appPath
        if (appPath != null) {
            appPathComponent.text = appPath
        }
    }

    override fun applyEditorTo(s: TSRunConfiguration) {
        s.appPath = appPathComponent.text.trim()
    }

    override fun createEditor(): JComponent =
        mainPanel
}
