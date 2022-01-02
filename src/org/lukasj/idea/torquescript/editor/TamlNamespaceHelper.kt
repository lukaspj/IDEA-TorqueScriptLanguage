package org.lukasj.idea.torquescript.editor

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.util.IncorrectOperationException
import com.intellij.xml.DefaultXmlNamespaceHelper
import org.lukasj.idea.torquescript.TamlFileType

class TamlNamespaceHelper : DefaultXmlNamespaceHelper() {
    override fun isAvailable(file: PsiFile) =
        file.virtualFile?.fileType == TamlFileType.INSTANCE

    override fun insertNamespaceDeclaration(
        file: XmlFile,
        editor: Editor?,
        possibleNamespaces: MutableSet<String>,
        nsPrefix: String?,
        runAfter: Runner<String, IncorrectOperationException>?
    ) {
    }
}