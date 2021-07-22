package org.lukasj.idea.torquescript.action

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.messages.MessageDialog
import com.intellij.openapi.util.Key
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.runner.LogConsoleType
import org.lukasj.idea.torquescript.runner.TSProcessHandler
import org.lukasj.idea.torquescript.runner.TSRunConfiguration
import org.lukasj.idea.torquescript.runner.TSTelnetClient
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
        val project = e.project
        val configuration =
            RunManager.getInstance(project!!)
                .allConfigurationsList
                .filterIsInstance<TSRunConfiguration>()
                .first { !it.appPath.isNullOrEmpty() }

        val debugMain = TSFileUtil.getPluginVirtualFile("scripts/buildexportsmain.tscript")

        val dir = configuration.workingDir
        val commandLine = GeneralCommandLine(configuration.appPath)
        commandLine.workDirectory = File(dir!!)
        commandLine.addParameters(debugMain)
        commandLine.addParameters("${dir.replace('\\', '/')}/engineApi.xml")


        try {
            val processHandler = TSProcessHandler(commandLine)

            processHandler.addProcessListener(object : ProcessListener {
                override fun startNotified(processEvent: ProcessEvent) {

                }

                override fun processTerminated(processEvent: ProcessEvent) {
                }

                override fun processWillTerminate(processEvent: ProcessEvent, b: Boolean) {

                }

                override fun onTextAvailable(processEvent: ProcessEvent, key: Key<*>) {
                    println(processEvent.text)
                }
            })
            processHandler.startNotify()

            val telnetClient = TSTelnetClient("127.0.0.1", 17432)
            telnetClient.connect()

            thread {
                while (!processHandler.isProcessTerminated) {
                    val line = telnetClient.outputQueue.poll(200, TimeUnit.MILLISECONDS)
                    if (line != null) {
                        println(line)
                    }
                }
            }

            ProgressManager.getInstance()
                .runProcessWithProgressAsynchronously(
                    object : Task.Backgroundable(project, "Rebuilding exports", true) {
                        override fun run(indicator: ProgressIndicator) {
                            try {
                                telnetClient.login("password")
                                telnetClient.eval("exportEngineAPIToXML().saveFile(\"${dir.replace('\\', '/')}/engineApi.xml\");\n" +
                                        "quit();")
                                telnetClient.resume()
                                if (processHandler.waitFor(3000)) {
                                    ApplicationManager.getApplication()
                                        .invokeLater {
                                            Messages.showMessageDialog(
                                                project,
                                                "Engine Exports were built",
                                                "Build Exports Done",
                                                Messages.getInformationIcon()
                                            )
                                        }
                                }
                            } catch (ex: Exception) {
                                Messages.showMessageDialog(
                                    project,
                                    "Something went wrong while building exports: $ex",
                                    "Build Exports Error",
                                    Messages.getErrorIcon()
                                )
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