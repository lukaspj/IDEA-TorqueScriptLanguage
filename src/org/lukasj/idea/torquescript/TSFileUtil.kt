package org.lukasj.idea.torquescript

import com.intellij.execution.RunManager
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.io.exists
import com.intellij.util.io.isDirectory
import org.lukasj.idea.torquescript.runner.TSRunConfiguration
import org.lukasj.idea.torquescript.taml.TamlModuleService
import java.io.File
import java.net.URI
import java.net.URL
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

    fun getSchemaFile(project: Project): URI =
        project.basePath
            ?.let { pwd ->
                Path.of(pwd, "engineApiSchema.xsd")
                    .let { schemaFilePath ->
                        if (schemaFilePath.exists()) {
                            schemaFilePath.toUri()
                        } else {
                            null
                        }
                    }
            }
            ?: this::class.java.getResource("/samples/engineApiSchema.xsd")!!.toURI()

    fun findFilesWithSuffix(root: VirtualFile, suffix: String) =
        findFiles(root) {
            it.name.endsWith(suffix)
        }

    fun findFiles(root: VirtualFile, pred: (VirtualFile) -> Boolean): List<VirtualFile> =
        if (root.isDirectory) {
            root.children
                .flatMap { findFiles(it, pred) }
        } else if (pred(root)) {
            listOf(root)
        } else {
            emptyList()
        }

    fun resolveScriptPath(context: PsiElement, path: String, isAssetPath: Boolean = false): VirtualFile? {
        return when {
            // ./ -> relative path
            path.startsWith("./") ->
                VfsUtil.findRelativeFile(
                    path,
                    context.containingFile.virtualFile
                )
            // / -> from root
            path.startsWith("/") ->
                VfsUtil.findRelativeFile(
                    toRelative(path),
                    VfsUtil.findFileByIoFile(File(context.project.basePath!!), true)
                )
            // : -> asset reference
            path.contains(":") ->
                path.split(':')
                    .let { split ->
                        context.project.getService(TamlModuleService::class.java)
                            .let { moduleService ->
                                moduleService.getAsset(split[0], split[1])
                                    ?.file
                            }
                    }
            // If nothing else works, just try to resolve from root.
            else ->
                VfsUtil.findRelativeFile(
                    path,
                    // Handle an inconsistency
                    if (isAssetPath) {
                        context.containingFile.virtualFile
                    } else {
                        VfsUtil.findFileByIoFile(File(context.project.basePath!!), true)
                    }
                )
        }
    }

        private fun toRelative(value: String) =
            when {
                value.startsWith("./") -> {
                    value
                }
                value.startsWith("/") -> {
                    ".$value"
                }
                else -> {
                    "./$value"
                }
            }
}