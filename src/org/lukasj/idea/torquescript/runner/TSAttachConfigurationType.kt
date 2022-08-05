package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import icons.TSIcons

class TSAttachConfigurationType : ConfigurationType {
    override fun getDisplayName() = "Attach to Torque App"

    override fun getConfigurationTypeDescription() = "Torque App Attach Configuration"

    override fun getIcon() = TSIcons.FILE

    override fun getId() = "TS_ATTACH_CONFIGURATION"

    override fun getConfigurationFactories(): Array<ConfigurationFactory> =
        arrayOf(TSAttachConfigurationFactory(this))

}