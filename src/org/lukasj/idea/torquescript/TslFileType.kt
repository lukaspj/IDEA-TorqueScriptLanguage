package org.lukasj.idea.torquescript

import com.intellij.openapi.fileTypes.LanguageFileType
import icons.TSIcons

class TslFileType : LanguageFileType(TslLanguage.INSTANCE) {
    override fun getName() = "Torque Shader Language"

    override fun getDescription() = "Torque Shader Language file"

    override fun getDefaultExtension() = "tlsl"

    override fun getIcon() = TSIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = TslFileType()
    }
}