package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.TamlAsset

@Suppress("UnstableApiUsage")
class ImportAssetDialog(val project: Project?, val asset: TamlAsset) : DialogWrapper(project, true) {
    init {
        title = "Import Asset"
        init()
    }

    override fun createCenterPanel() =
        panel {
            group("General Asset Information") {
                row("Asset Name") {
                    textField()
                        .bindText({ asset.assetName ?: "" }, { asset.assetName = it })
                }.rowComment("The name of the asset")
                row("Asset Type") {
                    comboBox(
                        project
                            ?.getService(EngineApiService::class.java)
                            ?.let { engineApi ->
                                engineApi.findClass("AssetBase")
                                    ?.let {
                                        engineApi.getSubclasses(it)
                                    }
                                    ?.map { it.name }
                                    ?.toTypedArray()
                            } ?: arrayOf(),
                        null
                    )
                        .bindItem({ asset.assetType }, { })
                        .enabled(false)
                }
                row("Asset Target File Path") {
                    textFieldWithBrowseButton { asset.assetFile.resolve(it.path).toString() }
                        .text(asset.assetFile.fileName.toString())
                        .enabled(false)
                }
                row("Description") {
                    textField()
                        .bindText({asset.assetDescription ?: ""}, {asset.assetDescription = it})
                }
                row("Category") {
                    textField()
                        .bindText({asset.assetCategory ?: ""}, {asset.assetCategory = it})
                }
                row {
                    checkBox("AutoUnload")
                        .bindSelected({asset.assetAutoUnload ?: false}, {asset.assetAutoUnload = it})
                    checkBox("Internal")
                        .bindSelected({asset.assetInternal ?: false}, {asset.assetInternal = it})
                    checkBox("Private")
                        .bindSelected({asset.assetPrivate ?: false}, {asset.assetPrivate = it})
                }
            }
            AssetImportPanel.getPanelFor(project, asset)
                ?.insertPanel(this)
        }
}