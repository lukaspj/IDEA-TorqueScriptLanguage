package org.lukasj.idea.torquescript.action

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Key
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.runner.TSProcessHandler
import org.lukasj.idea.torquescript.runner.TSRunConfiguration
import org.lukasj.idea.torquescript.runner.TSTelnetClient
import org.lukasj.idea.torquescript.telnet.TelnetConsoleService
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class RebuildExportsAction : AnAction() {
    override fun update(e: AnActionEvent) {
        val project = e.project

        if (project == null) {
            e.presentation.isEnabledAndVisible = e.project != null
        } else {
            e.presentation.isVisible = true
            e.presentation.isEnabled =
                RunManager.getInstance(project)
                    .allConfigurationsList
                    .filterIsInstance<TSRunConfiguration>()
                    .filter { !it.appPath.isNullOrEmpty() }
                    .any()
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val configuration =
            RunManager.getInstance(project)
                .allConfigurationsList
                .filterIsInstance<TSRunConfiguration>()
                .first { !it.appPath.isNullOrEmpty() }

        val dir = configuration.workingDir?.replace('\\', '/') ?: return

        try {
            ProgressManager.getInstance()
                .runProcessWithProgressAsynchronously(
                    object : Task.Backgroundable(project, "Rebuilding exports", true) {
                        override fun run(indicator: ProgressIndicator) {
                            try {
                                val success = project.getService(TelnetConsoleService::class.java)
                                    .runTelnetSession(project) { telnetClient ->
                                        telnetClient.eval(
                                            """setLogMode(6);
                                               ${'$'}pref::T2D::TAMLSchema = "$dir/engineApiSchema.xsd";
                                               ${'$'}pref::T3D::TAMLSchema = "$dir/engineApiSchema.xsd";
                                               exportEngineAPIToXML().saveFile("$dir/engineApi.xml");
                                               GenerateTamlSchema();
                                            """.split('\n')
                                                .joinToString(" ") { it.trim() }
                                        )
                                    }
                                ApplicationManager.getApplication()
                                    .invokeLater {
                                        if (success) {
                                            Messages.showMessageDialog(
                                                project,
                                                "Engine Exports were built",
                                                "Build Exports Done",
                                                Messages.getInformationIcon()
                                            )
                                        } else {
                                            Messages.showMessageDialog(
                                                project,
                                                "Engine failed to terminate, killing it manually.\nPlease check whether engineApi.xml was generated successfully",
                                                "Build Exports Timeout",
                                                Messages.getWarningIcon()
                                            )
                                        }
                                    }
                            } catch (ex: Exception) {
                                ApplicationManager.getApplication()
                                    .invokeLater {
                                        Messages.showMessageDialog(
                                            project,
                                            "Something went wrong while building exports: $ex",
                                            "Build Exports Error",
                                            Messages.getErrorIcon()
                                        )
                                    }
                            }
                        }
                    },
                    EmptyProgressIndicator()
                )
        } catch (ex: Exception) {
            Messages.showMessageDialog(
                project,
                "Something went wrong while building exports: $ex",
                "Build Exports Error",
                Messages.getErrorIcon()
            )
        }
    }
}