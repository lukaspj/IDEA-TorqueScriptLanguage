package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.TamlAsset

class ImportAssetDialog(val project: Project, val asset: TamlAsset, val children: List<TamlAsset>) :
    DialogWrapper(project, true) {
    init {
        title = "Import Asset"
        init()
    }

    override fun createCenterPanel() =
        panel {
            createPanelFor(this, asset, children)
        }

    fun createPanelFor(rowBuilder: Panel, asset: TamlAsset, children: List<TamlAsset>): Panel =
        rowBuilder
            .panel {
                group("General Asset Information") {
                    row("Asset Name") {
                        textField()
                            .bindText(
                                { asset.assetName ?: "" },
                                { asset.assetName = it }
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
                            .bindItem({ asset.assetType }, {})
                            .enabled(false)
                    }
                    row("Asset Target File Path") {
                        textFieldWithBrowseButton(
                            "Choose Asset Target File Path",
                            project,
                        ) { asset.assetFile.resolve(it.path).toString() }
                            .bindText(
                                { asset.assetFile.fileName.toString() },
                                { }
                            )
                            .enabled(false)
                    }
                    row("Description") {
                        textField()
                            .bindText(
                                { asset.assetDescription ?: "" },
                                { asset.assetDescription = it }
                            )
                    }
                    row("Category") {
                        textField()
                            .bindText(
                                { asset.assetCategory ?: "" },
                                { asset.assetCategory = it }
                            )
                    }
                    row {
                        checkBox("AutoUnload")
                            .bindSelected(
                                { asset.assetAutoUnload ?: false },
                                { asset.assetAutoUnload = it }
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

                if (children.isNotEmpty()) {
                    group("Child Assets") {
                        children.forEach {
                            collapsibleGroup(it.assetType) {
                                createPanelFor(this, it, listOf())
                            }
                        }
                    }
                }
            }
}