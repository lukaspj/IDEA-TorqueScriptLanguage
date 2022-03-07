package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.RowBuilder
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.ImageAsset
import org.lukasj.idea.torquescript.taml.MaterialAsset
import org.lukasj.idea.torquescript.taml.ShapeAsset
import java.nio.file.Path

class MaterialAssetImportPanel(val project: Project, val asset: MaterialAsset) : AssetImportPanel {
    override fun insertPanel(panel: RowBuilder) =
        panel.titledRow("Material") {
            row("Script File") {
                textFieldWithBrowseButton(
                    getter = { asset.scriptFile ?: "" },
                    setter = { asset.scriptFile = asset.assetFile.parent.relativize(Path.of(it)).toString() }
                ) { asset.assetFile.resolve(it.path).toString() }
            }
            row("Material Definition Name") {
                textField(
                    getter = { asset.materialDefinitionName ?: "" },
                    setter = { asset.materialDefinitionName = it }
                )
            }
        }
}