package org.lukasj.idea.torquescript.parser

import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.xml.XmlFileImpl
import com.intellij.psi.tree.IFileElementType
import org.lukasj.idea.torquescript.TamlLanguage

class TamlParserDefinition : XMLParserDefinition() {
    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return TamlFileImpl(viewProvider)
    }
}

class TamlFileImpl(viewProvider: FileViewProvider)
    : XmlFileImpl(viewProvider, FILE_TYPE) {

    companion object {
        val FILE_TYPE = IFileElementType(TamlLanguage.INSTANCE)
    }
}
