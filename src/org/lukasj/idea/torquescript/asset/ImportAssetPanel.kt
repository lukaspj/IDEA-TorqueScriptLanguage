package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.TamlAsset

class ImportAssetPanel(val project: Project, val asset: TamlAsset, val children: List<TamlAsset>) :
    DialogWrapper(project, true) {
    override fun createCenterPanel() =
        panel {
            group("General Asset Information") {
                row("Asset Name") {
                    textField()
                        .bindText(
                            getter = { asset.assetName ?: "" },
                            setter = { asset.assetName = it }
                        )
                    comment("The name of the asset")
                }
                row("Asset Type") {
                    comboBox(
                        project
                            .getService(EngineApiService::class.java)
                            ?.let { engineApi ->
                                engineApi.findClass("AssetBase")
                                    ?.let {
                                        engineApi.getSubclasses(it)
                                    }
                                    ?.map { it.name }
                            } ?: listOf()
                    )
                        .bindItem(
                            getter = { asset.assetType },
                            setter = {},
                        )
                        .enabled(false)
                }
                row("Asset Target File Path") {
                    textFieldWithBrowseButton { asset.assetFile.resolve(it.path).toString() }
                        .bindText(
                            getter = { asset.assetFile.fileName.toString() },
                            setter = { }
                        )
                        .enabled(false)
                }
                row("Description") {
                    textField()
                        .bindText(
                            getter = { asset.assetDescription ?: "" },
                            setter = { asset.assetDescription = it }
                        )
                }
                row("Category") {
                    textField()
                        .bindText(
                            getter = { asset.assetCategory ?: "" },
                            setter = { asset.assetCategory = it }
                        )
                }
                row {
                    checkBox("AutoUnload")
                        .bindSelected(
                            getter = { asset.assetAutoUnload ?: false },
                            setter = { asset.assetAutoUnload = it }
                        )
                    checkBox("Internal")
                        .bindSelected(
                            getter = { asset.assetInternal ?: false },
                            setter = { asset.assetInternal = it }
                        )
                    checkBox("Private")
                        .bindSelected(
                            getter = { asset.assetPrivate ?: false },
                            setter = { asset.assetPrivate = it }
                        )
                }
            }
            AssetImportPanel.getPanelFor(project, asset)
                ?.insertPanel(this)
            group("Child Assets") {
                children.forEach {
                    AssetImportPanel.getPanelFor(project, it)
                        ?.insertPanel(this)
                }
            }
        }
}