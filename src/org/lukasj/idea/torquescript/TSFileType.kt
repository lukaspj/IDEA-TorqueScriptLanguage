package org.lukasj.idea.torquescript

import com.intellij.openapi.fileTypes.LanguageFileType
import icons.TSIcons
import javax.swing.Icon

class TSFileType : LanguageFileType(TSLanguage.INSTANCE) {
    override fun getName() = "TorqueScript File"

    override fun getDescription() = "TorqueScript file"

    override fun getDefaultExtension() = "tscript"

    override fun getIcon() = TSIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = TSFileType()
    }
}