package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel
import org.jetbrains.annotations.ApiStatus
import javax.swing.JComponent

class TSAttachConfigurationSettingsEditor(val project: Project) : SettingsEditor<TSAttachConfiguration>() {
    private val model = TSAttachConfigurationEditorModel()

    private val mainPanel =
        panel {
            row("Host:") { textField(model::host) }
            row("Port:") { textField(model::port) }
            row("Password:") { textField(model::password) }
        }

    override fun resetEditorFrom(s: TSAttachConfiguration) {
        model.host = s.host
        model.port = s.port.toString()
        model.password = s.password

        mainPanel.reset()
    }

    override fun applyEditorTo(s: TSAttachConfiguration) {
        s.host = model.host
        s.port = model.port.toInt()
        s.password = model.password

        mainPanel.apply()
    }

    override fun createEditor(): JComponent =
        mainPanel
}

@ApiStatus.Internal
internal data class TSAttachConfigurationEditorModel(
    var host: String = "",
    var port: String = "",
    var password: String = "",
)
