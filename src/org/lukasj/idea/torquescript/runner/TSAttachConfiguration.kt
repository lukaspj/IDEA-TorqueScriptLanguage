package org.lukasj.idea.torquescript.runner

import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import kotlin.reflect.full.primaryConstructor

class TSAttachCommandLineState : RunProfileState {
    override fun execute(executor: Executor?, runner: ProgramRunner<*>): ExecutionResult? {
        TODO("Not yet implemented")
    }
}

class TSAttachConfiguration(project: Project, factory: ConfigurationFactory, name: String) :
    RunConfigurationBase<TSAttachCommandLineState>(project, factory, name) {

    override fun getOptions(): TSAttachConfigurationOptions {
        return super.getOptions() as TSAttachConfigurationOptions
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState =
        TSAttachCommandLineState()

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        // Hacky-workaround for legacy code
        try {
            Class.forName("org.lukasj.idea.torquescript.runner.TSLegacyAttachConfigurationSettingsEditor")
        } catch (ex: ClassNotFoundException) {
            Class.forName("org.lukasj.idea.torquescript.runner.TSAttachConfigurationSettingsEditor")
        }.kotlin.primaryConstructor!!.call(project) as SettingsEditor<out RunConfiguration>

    var host
        get() = options.host ?: "127.0.0.1"
        set(value) {
            options.host = value
        }

    var port
        get() = options.port
        set(value) {
            options.port = value
        }

    var password
        get() = options.password ?: "password"
        set(value) {
            options.password = value
        }
}
