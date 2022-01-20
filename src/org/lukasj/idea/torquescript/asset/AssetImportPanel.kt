package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.layout.LayoutBuilder
import com.intellij.ui.layout.Row
import org.lukasj.idea.torquescript.taml.ImageAsset
import org.lukasj.idea.torquescript.taml.TamlAsset

interface AssetImportPanel {
    fun insertPanel(panel: LayoutBuilder): Row

    companion object {
        fun getPanelFor(project: Project?, asset: TamlAsset) =
            when (asset) {
                is ImageAsset -> ImageAssetImportPanel(project, asset)
                else -> null
            }
    }
}