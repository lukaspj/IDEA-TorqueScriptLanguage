package org.lukasj.idea.torquescript.runner

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project

class TSRunConfiguration(project: Project, factory: ConfigurationFactory, name: String)
    : RunConfigurationBase<CommandLineState>(project, factory, name) {
    var appPath: String? = null

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? =
        TSCommandLineState(this, environment)

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        TSRunConfigurationSettingsEditor(project)
}
