package org.lukasj.idea.torquescript.taml

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.symbols.TSModificationTracker
import java.nio.file.Path

@Service
class TamlModuleService(private val project: Project) {
    private val modificationTracker = TSModificationTracker()

    private var cachedModuleList = CachedValuesManager.getManager(project)
        .createCachedValue {
            findModules()
                .let {
                    CachedValueProvider.Result.create(
                        it,
                        arrayOf<Any>(modificationTracker).plus(elements = it.toTypedArray())
                    )
                }
        }

    private val cachedModules =
        CachedValuesManager.getManager(project)
            .let { cachedValuesManager ->
                cachedModuleList.value.associateBy(
                    { it.nameWithoutExtension },
                    {
                        cachedValuesManager.createCachedValue {
                            CachedValueProvider.Result
                                .create(
                                    parseModule(it),
                                    arrayOf(modificationTracker, it)
                                )
                        }
                    })
            }

    private fun parseModule(file: VirtualFile): TamlModule =
        TamlModule.parse(file)!!

    fun dropCaches() {
        modificationTracker.count++
    }

    private fun findModules(): List<VirtualFile> =
        project.basePath
            ?.let { VfsUtil.findFile(Path.of(it), false) }
            ?.let { TSFileUtil.findFilesWithSuffix(it, ".module") }
            ?: emptyList()

    fun getModules(): List<TamlModule> =
        cachedModules
            .values
            .map { it.value }

    fun getModule(name: String): TamlModule? =
        cachedModules[name]?.value

    fun getAsset(moduleName: String, assetName: String): TamlAsset? =
        getAssets(moduleName)
            .firstOrNull { it.assetName == assetName }

    fun getAssets(moduleName: String): List<TamlAsset> =
        getModule(moduleName)
            ?.let { module ->
                listOf<TamlAsset>()
                    .plus(
                        module
                            .declaredAssetDeclarations
                            .filter { it.extension != null }
                            .flatMap { declaredAssets ->
                                if (declaredAssets.recurse == true) {
                                    if (declaredAssets.path != null) {
                                        VfsUtil.findRelativeFile(module.file.parent, declaredAssets.path)
                                    } else {
                                        module.file.parent
                                    }?.let { TSFileUtil.findFiles(it) { true } }
                                        ?: listOf()
                                } else {
                                    module.file.parent.children.toList()
                                }
                                    .filter { it.name.endsWith(declaredAssets.extension!!) }
                            }.mapNotNull {
                                TamlAsset.parse(it)
                            }
                    )
                    .plus(
                        module
                            .autoloadAssetDeclarations
                            .flatMap { autoloadAssets ->
                                if (autoloadAssets.recurse == true) {
                                    VfsUtil.findRelativeFile(module.file.parent, autoloadAssets.path ?: "")
                                        ?.let { TSFileUtil.findFiles(it) { true } }
                                        ?: listOf()
                                } else {
                                    module.file.parent.children.toList()
                                }.mapNotNull {
                                    TamlAsset.parse(it)
                                }
                                    .filter { autoloadAssets.assetType == null || it.assetType == autoloadAssets.assetType }
                            }
                    )
            } ?: listOf()

    fun getModuleForFile(file: Path) =
        getModules()
            .firstOrNull { module ->
                getAssets(module.moduleId!!)
                    .any { it.assetFile == file }
            }
}