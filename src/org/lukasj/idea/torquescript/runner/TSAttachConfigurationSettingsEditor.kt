package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.components.Service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import org.jetbrains.annotations.ApiStatus
import org.lukasj.idea.torquescript.TSFileUtil
import java.nio.file.Path
import javax.swing.JComponent

class TSAttachConfigurationSettingsEditor(val project: Project) : SettingsEditor<TSAttachConfiguration>() {
    private val model = TSAttachConfigurationEditorModel()
    private val mainPanel =
        panel {
            row("Host:") {
                textField()
                    .bindText(model::host)
                    .comment("Specify the host of the remote Torque App, typically 127.0.0.1")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
            row("Port:") {
                intTextField()
                    .bindIntText(model::port)
                    .comment("Specify the port of the remote Torque App")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
            row("Password:") {
                textField()
                    .bindText(model::password)
                    .comment("Specify the password for the Torque App telnet process")
                    .horizontalAlign(HorizontalAlign.FILL)
            }
        }

    override fun resetEditorFrom(s: TSAttachConfiguration) {
        model.host = s.host
        model.port = s.port
        model.password = s.password
        mainPanel.reset()
    }

    override fun applyEditorTo(s: TSAttachConfiguration) {
        s.host = model.host
        s.port = model.port
        s.password = model.password
        mainPanel.apply()
    }

    override fun createEditor(): JComponent =
        mainPanel
}

@ApiStatus.Internal
internal data class TSAttachConfigurationEditorModel(
    var host: String = "",
    var port: Int = 0,
    var password: String = "",
)
