package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import org.lukasj.idea.torquescript.taml.ImageAsset
import org.lukasj.idea.torquescript.taml.TamlAsset

interface AssetImportPanel {
    @Suppress("UnstableApiUsage")
    fun insertPanel(panel: Panel): Panel

    companion object {
        fun getPanelFor(project: Project?, asset: TamlAsset) =
            when (asset) {
                is ImageAsset -> ImageAssetImportPanel(project, asset)
                else -> null
            }
    }
}