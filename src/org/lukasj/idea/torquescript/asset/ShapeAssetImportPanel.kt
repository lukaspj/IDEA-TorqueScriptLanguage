package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.RowBuilder
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.ImageAsset
import org.lukasj.idea.torquescript.taml.ShapeAsset
import java.nio.file.Path

class ShapeAssetImportPanel(val project: Project, val asset: ShapeAsset) : AssetImportPanel {
    override fun insertPanel(panel: RowBuilder) =
        panel.titledRow("Shape") {
            row("File Name") {
                textFieldWithBrowseButton(
                    getter = { asset.fileName ?: "" },
                    setter = { asset.fileName = asset.assetFile.parent.relativize(Path.of(it)).toString() }

                ) { asset.assetFile.resolve(it.path).toString() }
            }
            row("Constructor File Name") {
                textFieldWithBrowseButton(
                    getter = { asset.constructorFileName ?: "" },
                    setter = { asset.constructorFileName = asset.assetFile.parent.relativize(Path.of(it)).toString() }

                ) { asset.assetFile.resolve(it.path).toString() }
            }
            row("Diffuse Imposter File Name") {
                textFieldWithBrowseButton(
                    getter = { asset.diffuseImposterFileName ?: "" },
                    setter = { asset.diffuseImposterFileName = asset.assetFile.parent.relativize(Path.of(it)).toString() }

                ) { asset.assetFile.resolve(it.path).toString() }
            }
            row("Normal Imposter File Name") {
                textFieldWithBrowseButton(
                    getter = { asset.normalImposterFileName ?: "" },
                    setter = { asset.normalImposterFileName = asset.assetFile.parent.relativize(Path.of(it)).toString() }

                ) { asset.assetFile.resolve(it.path).toString() }
            }
        }
}