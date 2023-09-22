package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindText
import org.lukasj.idea.torquescript.taml.MaterialAsset

class MaterialAssetImportPanel(val project: Project, val asset: MaterialAsset) : AssetImportPanel {
    override fun insertPanel(panel: Panel) =
        panel.group("Material") {
            row("Map To Material Name") {
                textField()
                    .bindText(
                        getter = { asset.mapTo ?: "" },
                        setter = { asset.mapTo = it }
                    )
                    .enabled(asset.mapTo == null)
            }
            row("Material Definition Name") {
                textField()
                    .bindText(
                        getter = { asset.assetName ?: "" },
                        setter = { asset.materialDefinitionName = it }
                    )
                    .enabled(false)
            }
        }
}