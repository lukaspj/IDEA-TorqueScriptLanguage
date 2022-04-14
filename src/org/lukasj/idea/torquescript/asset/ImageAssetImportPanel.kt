package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.ImageAsset
import java.nio.file.Path

class ImageAssetImportPanel(val project: Project, val asset: ImageAsset) : AssetImportPanel {
    override fun insertPanel(panel: Panel) =
        panel.group("Image") {
            row("Image Type") {
                comboBox(
                    project.getService(EngineApiService::class.java)
                        ?.findEnum("ImageAssetType")
                        ?.values
                        ?.map { it.name }
                        ?: listOf(
                            "Albedo",
                            "Normal",
                            "ORMConfig",
                            "Roughness",
                            "AO",
                            "Metalness",
                            "Glow",
                            "GUI",
                            "Particle",
                            "Decal",
                            "Cubemap",
                        ),
                ).bindItem(
                    getter = { asset.imageType },
                    setter = { asset.imageType = it }
                )
            }
            row("Image File Path") {
                textFieldWithBrowseButton() { asset.assetFile.resolve(it.path).toString() }
                    .bindText(
                        getter = { asset.imageFilePath.toString() },
                        setter = { asset.imageFilePath = asset.assetFile.parent.relativize(Path.of(it)).toString() }
                    )
            }
            row {
                checkBox("Is HDR Image")
                    .bindSelected(
                        getter = { asset.isHdrImage ?: false },
                        setter = { asset.isHdrImage = it }
                    )
                checkBox("Use mips")
                    .bindSelected(
                        getter = { asset.useMips ?: false },
                        setter = { asset.useMips = it }
                    )
            }
        }
}