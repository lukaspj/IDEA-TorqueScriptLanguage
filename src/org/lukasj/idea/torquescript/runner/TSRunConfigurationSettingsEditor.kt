package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import org.jetbrains.annotations.ApiStatus
import org.lukasj.idea.torquescript.TSFileUtil
import java.nio.file.Path
import javax.swing.JComponent

class TSRunConfigurationSettingsEditor(val project: Project) : SettingsEditor<TSRunConfiguration>() {
    private val model = TSRunConfigurationEditorModel()
    private val mainPanel =
        panel {
            row("Executable:") {
                textFieldWithBrowseButton(
                    "App Path",
                    project,
                    FileChooserDescriptorFactory.createSingleFileDescriptor(),
                ) { TSFileUtil.relativePathFromRoot(project, it).toString() }
                    .bindText(model::appPath)
                    .comment("Specify path to Torque App executable")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
            row("Working Directory:") {
                textFieldWithBrowseButton(
                    "Working Directory",
                    project,
                    FileChooserDescriptorFactory.createSingleFileDescriptor()
                ) { TSFileUtil.relativePathFromRoot(project, it).toString() }
                    .bindText(model::workingDirectory)
                    .comment("Specify path to the working directory")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
            row("Main Script:") {
                textFieldWithBrowseButton(
                    "Main Script",
                    project,
                    FileChooserDescriptorFactory.createSingleFileDescriptor()
                ) { TSFileUtil.relativePathFromRoot(project, it).toString() }
                    .bindText(model::mainScript)
                    .comment("Specify path to the main script file")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
        }

    override fun resetEditorFrom(s: TSRunConfiguration) {
        val appPath = s.appPath
        if (appPath != null) {
            model.appPath = TSFileUtil.relativePathFromRoot(project, Path.of(appPath)).toString().replace('\\', '/')
        }
        model.workingDirectory = TSFileUtil.relativePathFromRoot(project, Path.of(s.workingDirectory)).toString().replace('\\', '/')
        model.mainScript = TSFileUtil.relativePathFromRoot(project, Path.of(s.mainScript)).toString().replace('\\', '/')
        mainPanel.reset()
    }

    override fun applyEditorTo(s: TSRunConfiguration) {
        s.appPath = TSFileUtil.absolutePathFromRoot(project, Path.of(model.appPath.trim())).toString().replace('\\', '/')
        s.workingDirectory = TSFileUtil.absolutePathFromRoot(project, Path.of(model.workingDirectory.trim())).toString().replace('\\', '/')
        s.mainScript = TSFileUtil.absolutePathFromRoot(project, Path.of(model.mainScript.trim())).toString().replace("\\", "/")
        mainPanel.apply()
    }

    override fun createEditor(): JComponent =
        mainPanel
}

@ApiStatus.Internal
internal data class TSRunConfigurationEditorModel(
    var appPath: String = "",
    var workingDirectory: String = "",
    var mainScript: String = "",
)
