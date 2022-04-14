package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.Row
import org.lukasj.idea.torquescript.taml.ImageAsset
import org.lukasj.idea.torquescript.taml.MaterialAsset
import org.lukasj.idea.torquescript.taml.ShapeAsset
import org.lukasj.idea.torquescript.taml.TamlAsset

interface AssetImportPanel {
    fun insertPanel(panel: Panel): Row

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