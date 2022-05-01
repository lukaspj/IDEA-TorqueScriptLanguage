package org.lukasj.idea.torquescript

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
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

    fun relativePathFromRoot(project: Project, virtualFile: VirtualFile) =
        relativePathFromRoot(project, Path.of(virtualFile.path))

    fun relativePathFromRoot(project: Project, path: Path): Path =
        Path.of(project.basePath!!)
            .relativize(path)

    fun resolveScriptPath(context: PsiElement, path: String, isAssetPath: Boolean = false) =
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
                Path.of(project.basePath!!)
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
                    Path.of(project.basePath!!)
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