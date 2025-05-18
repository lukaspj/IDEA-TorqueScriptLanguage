package org.lukasj.idea.torquescript.telnet

import com.intellij.execution.RunManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Key
import kotlinx.coroutines.runBlocking
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.runner.TSProcessHandler
import org.lukasj.idea.torquescript.runner.TSRunConfiguration
import java.io.File

@Service(Service.Level.PROJECT)
class TelnetConsoleService {
    fun runTelnetSession(project: Project, timeout: Long = 3000, sessionFn: (TSTelnetClient) -> Unit): Boolean {
        val configuration =
            RunManager.getInstance(project)
                .allConfigurationsList
                .filterIsInstance<TSRunConfiguration>()
                .first { !it.appPath.isNullOrEmpty() }

        val debugMain = TSFileUtil.getPluginVirtualFile("scripts/debuggermain.tscript")

        val dir = configuration.workingDirectory ?: "./"
        val commandLine = GeneralCommandLine(configuration.appPath)
        commandLine.workDirectory = File(dir)
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
            runBlocking {
                telnetClient.login("password")
                telnetClient.resume()
                sessionFn(telnetClient)
                telnetClient.eval("quit();")
            }
            return processHandler.waitFor(timeout)
        } catch (ex: Exception) {
            ApplicationManager.getApplication()
                .invokeLater {
                    Messages.showMessageDialog(
                        project,
                        "Something went wrong while running a T3D session: $ex",
                        "Telnet Session Error",
                        Messages.getErrorIcon()
                    )
                }
            return false
        } finally {
            processHandler.killProcess()
        }
    }
}
