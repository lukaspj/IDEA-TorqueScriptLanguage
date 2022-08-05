package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.layout.panel
import org.lukasj.idea.torquescript.TSFileUtil
import java.nio.file.Path
import javax.swing.JComponent

class TSLegacyRunConfigurationSettingsEditor(val project: Project) : SettingsEditor<TSRunConfiguration>() {
    private val appPathComponent = TextFieldWithBrowseButton()
    private val workingDirectoryComponent = TextFieldWithBrowseButton()
    private val mainScriptComponent = TextFieldWithBrowseButton()

    private val mainPanel =
        panel {
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
            appPathComponent.text = TSFileUtil.relativePathFromRoot(project, Path.of(appPath)).toString().replace('\\', '/')
        }
        workingDirectoryComponent.text =
            TSFileUtil.relativePathFromRoot(project, Path.of(s.workingDirectory)).toString().replace('\\', '/')
        mainScriptComponent.text = TSFileUtil.relativePathFromRoot(project, Path.of(s.mainScript)).toString().replace('\\', '/')
        mainPanel.reset()
    }

    override fun applyEditorTo(s: TSRunConfiguration) {
        s.appPath =
            TSFileUtil.absolutePathFromRoot(project, Path.of(appPathComponent.text.trim())).toString().replace('\\', '/')
        s.workingDirectory = TSFileUtil.absolutePathFromRoot(project, Path.of(workingDirectoryComponent.text.trim())).toString()
            .replace('\\', '/')
        s.mainScript =
            TSFileUtil.absolutePathFromRoot(project, Path.of(mainScriptComponent.text.trim())).toString().replace("\\", "/")
        mainPanel.apply()
    }

    override fun createEditor(): JComponent =
        mainPanel
}
