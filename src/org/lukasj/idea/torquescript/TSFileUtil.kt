package org.lukasj.idea.torquescript

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.util.io.exists
import io.sentry.SentryLevel
import org.lukasj.idea.torquescript.taml.TamlModuleService
import java.io.File
import java.net.URI
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

    fun getRootDirectory(project: Project): String =
        getPossibleRootDirectories(project)
            .let {
                if (it.size == 1) {
                    it.first().path
                } else {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("TorqueScript")
                        .createNotification(
                            "Automatically detected ${it.size} possible Torque3D content roots, " +
                                    "don't have any logic for choosing between them so selected ${it.first().path}",
                            NotificationType.WARNING
                        )
                        .notify(project)
                    SentryService.getHub()
                        .captureMessage(
                            "Automatically detected ${it.size} possible Torque3D content roots, " +
                                    "don't have any logic for choosing between them so selected ${it.first().path}",
                            SentryLevel.WARNING
                        )
                    it.first().path
                }
            }

    fun getPossibleRootDirectories(project: Project) =
        ModuleManager.getInstance(project)
            .modules
            .flatMap { module ->
                module.rootManager.contentRoots.filter { contentRoot ->
                    contentRoot.children.any { it.name in listOf("main.cs", "main.tscript") }
                }
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
        getRootDirectory(project)
            .let { pwd ->
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

    fun relativePathFromRoot(project: Project, virtualFile: VirtualFile) =
        relativePathFromRoot(project, Path.of(virtualFile.path))

    fun relativePathFromRoot(project: Project, path: Path): Path =
        if (path.isAbsolute) {
            Path.of(getRootDirectory(project))
                .relativize(path)
        } else {
            path
        }

    fun absolutePathFromRoot(project: Project, virtualFile: VirtualFile) =
        absolutePathFromRoot(project, Path.of(virtualFile.path))

    fun absolutePathFromRoot(project: Project, path: Path): Path =
        if (!path.isAbsolute) {
            Path.of(getRootDirectory(project))
                .resolve(path)
                .toAbsolutePath()
        } else {
            path
        }

    fun resolveScriptPath(context: PsiElement, path: String, isAssetPath: Boolean = false): Path? =
        context.containingFile.virtualFile
            ?.let {
                if (it.parent != null) {
                    resolveScriptPath(
                        Path.of(it.parent.path),
                        context.project,
                        path,
                        isAssetPath
                    )
                } else {
                    SentryService.getHub()
                        .captureMessage(
                            "The parent of ${it.path} was null, couldn't resolve path $path",
                            SentryLevel.WARNING
                        )
                    return@let null
                }
            }

    fun resolveScriptPath(relativeFile: Path, project: Project, path: String, isAssetPath: Boolean = false): Path? {
        return when {
            path.contains("*") ->
                null
            // ./ -> relative path
            path.startsWith("./") ->
                relativeFile
                    .resolve(path)
            // / -> from root
            path.startsWith("/") ->
                Path.of(getRootDirectory(project))
                    .resolve(toRelative(path))
            // : -> asset reference
            path.contains(":") ->
                path.split(':')
                    .let { split ->
                        project.getService(TamlModuleService::class.java)
                            .let { moduleService ->
                                moduleService.getAsset(split[0], split[1])
                                    ?.assetFile
                            }
                    }
            // If nothing else works, just try to resolve from root.
            else ->
                // Handle an inconsistency
                if (isAssetPath) {
                    relativeFile
                } else {
                    Path.of(getRootDirectory(project))
                }.resolve(path)
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

    fun assetRelativePathFromFile(path: String, file: Path) =
        if (path.contains(':')) {
            path
        } else {
            "@assetFile=$path"
        }
}