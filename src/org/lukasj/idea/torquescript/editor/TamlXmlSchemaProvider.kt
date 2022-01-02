package org.lukasj.idea.torquescript.editor

import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.xml.XmlSchemaProvider
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.TamlFileType

class TamlXmlSchemaProvider : XmlSchemaProvider() {
    override fun isAvailable(file: XmlFile) =
        file.virtualFile?.fileType == TamlFileType.INSTANCE

    override fun getSchema(url: String, module: Module?, baseFile: PsiFile) =
        TSFileUtil.getSchemaFile(baseFile.project)
            ?.let {
                PsiManager.getInstance(baseFile.project).findFile(VfsUtil.findFileByURL(it.toUri().toURL())!!)?.copy() as XmlFile
            }
}