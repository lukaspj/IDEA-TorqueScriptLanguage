package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project

class TSAttachConfigurationFactory(type: ConfigurationType) :
    ConfigurationFactory(type) {
    override fun createTemplateConfiguration(project: Project) =
        TSAttachConfiguration(project, this, "Attach to Torque3D")

    override fun getName() = "Torque App attach configuration factory"

    override fun getId() = "TS_ATTACH_FACTORY"

    override fun getOptionsClass(): Class<out BaseState> {
        return TSAttachConfigurationOptions::class.java
    }
}
