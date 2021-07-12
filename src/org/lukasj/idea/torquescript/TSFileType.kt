package org.lukasj.idea.torquescript

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class TSFileType : LanguageFileType(TSLanguage.INSTANCE) {
    override fun getName(): String {
        return "TorqueScript File"
    }

    override fun getDescription(): String {
        return "TorqueScript file"
    }

    override fun getDefaultExtension(): String {
        return "tscript"
    }

    override fun getIcon(): Icon? {
        return TSIcons.FILE
    }

    companion object {
        @JvmField
        val INSTANCE = TSFileType()
    }
}