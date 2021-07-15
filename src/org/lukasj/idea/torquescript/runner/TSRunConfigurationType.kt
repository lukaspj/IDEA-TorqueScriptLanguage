package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import org.lukasj.idea.torquescript.TSIcons

class TSRunConfigurationType : ConfigurationType {
    override fun getDisplayName() = "Torque App"

    override fun getConfigurationTypeDescription() = "Torque App Run Configuration"

    override fun getIcon() = TSIcons.FILE

    override fun getId() = "TS_RUN_CONFIGURATION"

    override fun getConfigurationFactories(): Array<ConfigurationFactory> =
        arrayOf(TSRunConfigurationFactory(this))

}