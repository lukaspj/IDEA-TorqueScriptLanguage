package org.lukasj.idea.torquescript.asset

import com.intellij.openapi.vfs.VirtualFile

class AssetImporter {
    fun Accepts(file: VirtualFile) =
        when (file.extension) {
            "png" -> true
            else -> false
        }

    fun Import(file: VirtualFile) {
        TODO("Not yet implemented")
    }
}