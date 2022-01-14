package org.lukasj.idea.torquescript

import com.intellij.ide.highlighter.XmlLikeFileType
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import icons.TSIcons

class TSFileType : LanguageFileType(TSLanguage.INSTANCE) {
    override fun getName() = "TorqueScript"

    override fun getDescription() = "TorqueScript file"

    override fun getDefaultExtension() = "tscript"

    override fun getIcon() = TSIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = TSFileType()
    }
}

class TamlFileType : XmlLikeFileType(TamlLanguage.INSTANCE) {
    override fun getName() = "TAML"

    override fun getDescription() = "TAML file"

    override fun getDisplayName() = "TAML"

    override fun getDefaultExtension() = "taml"

    override fun getIcon() = TSIcons.TAML

    companion object {
        @JvmField
        val INSTANCE = TamlFileType()
    }
}

class ModuleFileType : XmlLikeFileType(TamlLanguage.INSTANCE) {
    override fun getName() = "Torque3D Module Description"

    override fun getDescription() = "Torque3D module description"

    override fun getDisplayName() = "module"

    override fun getDefaultExtension() = "module"

    override fun getIcon() = TSIcons.TAML

    companion object {
        @JvmField
        val INSTANCE = ModuleFileType()
    }
}