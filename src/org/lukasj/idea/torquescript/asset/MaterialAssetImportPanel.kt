package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindText
import org.lukasj.idea.torquescript.taml.MaterialAsset
import java.nio.file.Path

class MaterialAssetImportPanel(val project: Project, val asset: MaterialAsset) : AssetImportPanel {
    override fun insertPanel(panel: Panel) =
        panel.group("Material") {
            row("Script File") {
                textFieldWithBrowseButton { asset.assetFile.resolve(it.path).toString() }
                    .bindText(
                        getter = { asset.scriptFile ?: "" },
                        setter = { asset.scriptFile = asset.assetFile.parent.relativize(Path.of(it)).toString() }
                    )
            }
            row("Material Definition Name") {
                textField()
                    .bindText(
                        getter = { asset.materialDefinitionName ?: "" },
                        setter = { asset.materialDefinitionName = it }
                    )
            }
        }
}