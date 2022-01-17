package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.*
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.ImageAsset
import org.lukasj.idea.torquescript.taml.TamlAsset
import java.nio.file.Path

class ImageAssetImportPanel(val project: Project?, val asset: ImageAsset) : AssetImportPanel {
    @Suppress("UnstableApiUsage")
    override fun insertPanel(panel: Panel) =
        panel.group("Image") {
            row("Image Type") {
                comboBox(
                    project?.getService(EngineApiService::class.java)
                        ?.findEnum("ImageAssetType")
                        ?.values
                        ?.map { it.name }
                        ?.toTypedArray()
                        ?: arrayOf(
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
                    null
                )
                    .bindItem({ asset.imageType }, { asset.imageType = it })
            }
            row("Image File Path") {
                textFieldWithBrowseButton { asset.assetFile.resolve(it.path).toString() }
                    .text(asset.imageFilePath.toString())
                    .bindText({ asset.imageFilePath.toString() }, { asset.imageFilePath = Path.of(it).toString() })
            }
            row {
                checkBox("Is HDR Image")
                    .bindSelected({ asset.isHdrImage ?: false }, { asset.isHdrImage = it })
                checkBox("Use mips")
                    .bindSelected({ asset.useMips ?: false }, { asset.useMips = it })
            }
        }
}