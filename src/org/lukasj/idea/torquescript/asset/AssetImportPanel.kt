package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.Row
import com.intellij.ui.layout.RowBuilder
import org.lukasj.idea.torquescript.taml.ImageAsset
import org.lukasj.idea.torquescript.taml.MaterialAsset
import org.lukasj.idea.torquescript.taml.ShapeAsset
import org.lukasj.idea.torquescript.taml.TamlAsset

interface AssetImportPanel {
    fun insertPanel(panel: RowBuilder): Row

    companion object {
        fun getPanelFor(project: Project, asset: TamlAsset) =
            when (asset) {
                is ImageAsset -> ImageAssetImportPanel(project, asset)
                is ShapeAsset -> ShapeAssetImportPanel(project, asset)
                is MaterialAsset -> MaterialAssetImportPanel(project, asset)
                else -> null
            }
    }
}