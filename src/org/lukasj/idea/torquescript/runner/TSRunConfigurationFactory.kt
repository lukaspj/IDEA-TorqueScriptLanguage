package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class TSRunConfigurationFactory(type: ConfigurationType) :
    ConfigurationFactory(type) {
    override fun createTemplateConfiguration(project: Project) =
        TSRunConfiguration(project, this, "Torque App")

    override fun getName() = "Torque App configuration factory"

    override fun getId() = "TS_APP_FACTORY"

    override fun getOptionsClass(): Class<out BaseState> {
        return TSRunConfigurationOptions::class.java
    }
}
