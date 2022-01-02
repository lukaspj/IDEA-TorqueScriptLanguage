package org.lukasj.idea.torquescript

import com.intellij.ide.highlighter.XmlLikeFileType
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import icons.TSIcons

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

class TamlFileType : XmlLikeFileType(XMLLanguage.INSTANCE) {
    override fun getName() = "TAML File"

    override fun getDescription() = "TAML file"

    override fun getDefaultExtension() = "taml"

    override fun getIcon() = TSIcons.TAML

    companion object {
        @JvmField
        val INSTANCE = TamlFileType()
    }
}