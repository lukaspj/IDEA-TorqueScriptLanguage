package org.lukasj.idea.torquescript

import com.intellij.execution.RunManager
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.io.exists
import org.lukasj.idea.torquescript.runner.TSRunConfiguration
import java.io.File
import java.nio.file.Path

object TSFileUtil {
    val pluginVirtualDirectory: VirtualFile?
        get() {
            val descriptor = PluginManagerCore.getPlugin(PluginId.getId("org.lukasj.idea.torquescript"))
            if (descriptor != null) {
                val pluginPath = descriptor.pluginPath

                val url = VfsUtil.pathToUrl(pluginPath.toAbsolutePath().toString())

                return VirtualFileManager.getInstance().findFileByUrl(url)
            }

            return null
        }

    fun getPluginVirtualFile(path: String): String? {
        val directory = pluginVirtualDirectory
        if (directory != null) {
            var fullPath = directory.path + "/classes/" + path
            if (File(fullPath).exists())
                return fullPath
            fullPath = directory.path + "/" + path
            if (File(fullPath).exists())
                return fullPath
        }
        return null
    }

    fun getSchemaFile(project: Project): Path? =
        RunManager.getInstance(project)
            .allConfigurationsList
            .filterIsInstance<TSRunConfiguration>()
            .first { !it.appPath.isNullOrEmpty() }
            .workingDir
            ?.let { pwd ->
                Path.of(pwd, "engineApiSchema.xsd")
                    .let { schemaFilePath ->
                        if (schemaFilePath.exists()) {
                            schemaFilePath
                        } else {
                            null
                        }
                    }
            }
}