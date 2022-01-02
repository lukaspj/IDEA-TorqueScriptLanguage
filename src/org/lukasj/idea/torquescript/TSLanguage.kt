package org.lukasj.idea.torquescript

import com.intellij.lang.Language
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.psi.FileViewProvider
import com.intellij.psi.impl.source.xml.XmlFileImpl
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType

class TSLanguage : Language("TorqueScript") {
    companion object {
        @JvmField
        val INSTANCE = TSLanguage()
    }
}
