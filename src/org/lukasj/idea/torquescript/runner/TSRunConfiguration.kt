package org.lukasj.idea.torquescript.runner

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import java.nio.file.Path

class TSRunConfiguration(project: Project, factory: ConfigurationFactory, name: String) :
    RunConfigurationBase<CommandLineState>(project, factory, name) {

    override fun getOptions(): TSRunConfigurationOptions {
        return super.getOptions() as TSRunConfigurationOptions
    }

    var appPath
        get() = options.appPath
        set(value) {
            options.appPath = value
        }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? =
        TSCommandLineState(this, environment)

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        TSRunConfigurationSettingsEditor(project)

    var workingDir: String? = null
        get() {
            val wd = field
            if (wd == null || wd.isEmpty()) {
                field = defaultWorkingDir
                return defaultWorkingDir
            }
            return wd
        }

    private val defaultWorkingDir: String
        // On MacOS, executable is not necessarily placed in root, so always use project.basePath as the root.
        // At least as long as it's a base assumption, that users open the game/ folder in IDEA.
        get() = project.basePath!!
}
