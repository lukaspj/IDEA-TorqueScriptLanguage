package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindText
import org.lukasj.idea.torquescript.taml.ShapeAsset
import java.nio.file.Path

class ShapeAssetImportPanel(val project: Project, val asset: ShapeAsset) : AssetImportPanel {
    override fun insertPanel(panel: Panel) =
        panel.group("Shape") {
            row("File Name") {
                textFieldWithBrowseButton { asset.assetFile.resolve(it.path).toString() }
                    .bindText(
                        getter = { asset.fileName ?: "" },
                        setter = { asset.fileName = asset.assetFile.parent.relativize(Path.of(it)).toString() }
                    )
            }
            row("Constructor File Name") {
                textFieldWithBrowseButton { asset.assetFile.resolve(it.path).toString() }
                    .bindText(
                        getter = { asset.constructorFileName ?: "" },
                        setter = { asset.constructorFileName = asset.assetFile.parent.relativize(Path.of(it)).toString() }
                    )
            }
            row("Diffuse Imposter File Name") {
                textFieldWithBrowseButton { asset.assetFile.resolve(it.path).toString() }
                    .bindText(
                        getter = { asset.diffuseImposterFileName ?: "" },
                        setter = { asset.diffuseImposterFileName = asset.assetFile.parent.relativize(Path.of(it)).toString() }
                    )
            }
            row("Normal Imposter File Name") {
                textFieldWithBrowseButton { asset.assetFile.resolve(it.path).toString() }
                    .bindText(
                        getter = { asset.normalImposterFileName ?: "" },
                        setter = { asset.normalImposterFileName = asset.assetFile.parent.relativize(Path.of(it)).toString() }
                    )
            }
        }
}