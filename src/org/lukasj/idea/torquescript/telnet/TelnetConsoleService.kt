package org.lukasj.idea.torquescript.telnet

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Key
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.runner.TSProcessHandler
import org.lukasj.idea.torquescript.runner.TSRunConfiguration
import org.lukasj.idea.torquescript.runner.TSTelnetClient
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Service
class TelnetConsoleService {
    fun runTelnetSession(project: Project, timeout: Long = 3000, sessionFn: (TSTelnetClient) -> Unit): Boolean {
        val configuration =
            RunManager.getInstance(project)
                .allConfigurationsList
                .filterIsInstance<TSRunConfiguration>()
                .first { !it.appPath.isNullOrEmpty() }

        val debugMain = TSFileUtil.getPluginVirtualFile("scripts/debuggermain.tscript")

        val dir = configuration.workingDir
        val commandLine = GeneralCommandLine(configuration.appPath)
        commandLine.workDirectory = File(dir!!)
        commandLine.addParameters(debugMain)
        commandLine.addParameters("${dir.replace('\\', '/')}/engineApi.xml")

        val processHandler = TSProcessHandler(commandLine)

        try {
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
            telnetClient.login("password")
            telnetClient.resume()
            sessionFn(telnetClient)
            telnetClient.eval("quit();")
            return processHandler.waitFor(timeout)
        } catch (ex: Exception) {
            Messages.showMessageDialog(
                project,
                "Something went wrong while running a T3D session: $ex",
                "Telnet Session Error",
                Messages.getErrorIcon()
            )
            return false
        } finally {
            processHandler.killProcess()
        }
    }
}
