package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.layout.PropertyBinding
import com.intellij.ui.layout.panel
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.TamlAsset

class ImportAssetDialog(val project: Project?, val asset: TamlAsset) : DialogWrapper(project, true) {
    init {
        title = "Import Asset"
        init()
    }

    override fun createCenterPanel() =
        panel {
            titledRow("General Asset Information") {
                row("Asset Name") {
                    textField(PropertyBinding(
                        get = { asset.assetName ?: "" },
                        set = { asset.assetName = it }
                    ));
                    comment("The name of the asset")
                }
                row("Asset Type") {
                    comboBox(
                        CollectionComboBoxModel(
                            project
                                ?.getService(EngineApiService::class.java)
                                ?.let { engineApi ->
                                    engineApi.findClass("AssetBase")
                                        ?.let {
                                            engineApi.getSubclasses(it)
                                        }
                                        ?.map { it.name }
                                } ?: listOf()
                        ),
                        getter = { asset.assetType },
                        setter = {},
                        null
                    )
                        .enabled(false)
                }
                row("Asset Target File Path") {
                    textFieldWithBrowseButton(
                        getter = { asset.assetFile.fileName.toString() },
                        setter = { }
                    ) { asset.assetFile.resolve(it.path).toString() }
                        .enabled(false)
                }
                row("Description") {
                    textField(
                        getter = { asset.assetDescription ?: "" },
                        setter = { asset.assetDescription = it }
                    )
                }
                row("Category") {
                    textField(
                        getter = { asset.assetCategory ?: "" },
                        setter = { asset.assetCategory = it }
                    )
                }
                row {
                    checkBox("AutoUnload",
                        getter = { asset.assetAutoUnload ?: false },
                        setter = { asset.assetAutoUnload = it }
                        )
                    checkBox("Internal",
                        getter = { asset.assetInternal ?: false },
                        setter = { asset.assetInternal = it }
                    )
                    checkBox("Private",
                        getter = { asset.assetPrivate ?: false },
                        setter = { asset.assetPrivate = it }
                    )
                }
            }
            AssetImportPanel.getPanelFor(project, asset)
                ?.insertPanel(this)
        }
}