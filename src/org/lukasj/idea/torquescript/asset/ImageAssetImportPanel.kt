package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.layout.LayoutBuilder
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.taml.ImageAsset
import java.nio.file.Path

class ImageAssetImportPanel(val project: Project?, val asset: ImageAsset) : AssetImportPanel {
    override fun insertPanel(panel: LayoutBuilder) =
        panel.titledRow("Image") {
            row("Image Type") {
                comboBox(
                    CollectionComboBoxModel(
                        project?.getService(EngineApiService::class.java)
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
                            )
                    ),
                    getter = { asset.imageType },
                    setter = { asset.imageType = it },
                    null
                )
            }
            row("Image File Path") {
                textFieldWithBrowseButton(
                    getter = { asset.imageFilePath.toString() },
                    setter = { asset.imageFilePath = Path.of(it).toString() }

                ) { asset.assetFile.resolve(it.path).toString() }
            }
            row {
                checkBox("Is HDR Image",
                    getter = { asset.isHdrImage ?: false },
                    setter = { asset.isHdrImage = it }
                )
                checkBox("Use mips",
                    getter = { asset.useMips ?: false },
                    setter = { asset.useMips = it }
                )
            }
        }
}