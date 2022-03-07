package org.lukasj.idea.torquescript.runner

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
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

    var workingDirectory: String
        get() {
            if (options.workingDirectory == null || options.workingDirectory!!.isEmpty()) {
                return defaultWorkingDir
            }
            return options.workingDirectory!!
        }
        set(value) {
            options.workingDirectory = value
        }

    var mainScript: String
        get() {
            if (options.mainScript == null || options.mainScript!!.isEmpty()) {
                return VfsUtil.findFile(Path.of(workingDirectory, "main.cs"), true)?.path
                    ?: VfsUtil.findFile(Path.of(workingDirectory, "main.tscript"), true)?.path
                    ?: Path.of(defaultWorkingDir, "main.tscript").toString()
            }
            return options.mainScript!!
        }
        set(value) {
            options.mainScript = value
        }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? =
        TSCommandLineState(this, environment)

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> =
        TSRunConfigurationSettingsEditor(project)

    private val defaultWorkingDir: String
        // On MacOS, executable is not necessarily placed in root, so always use project.basePath as the root.
        // At least as long as it's a base assumption, that users open the game/ folder in IDEA.
        get() = project.basePath!!
}
