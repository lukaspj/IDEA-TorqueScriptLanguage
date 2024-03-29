package org.lukasj.idea.torquescript.runner

import com.intellij.execution.ExecutionResult
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import java.nio.file.Path
import kotlin.reflect.full.createInstance
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
        TSAttachConfigurationSettingsEditor(project)

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
