package org.lukasj.idea.torquescript.editor

import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.xml.impl.schema.XmlNSDescriptorImpl
import org.lukasj.idea.torquescript.TSFileUtil

class TamlNamespaceDescriptor : XmlNSDescriptorImpl() {
    override fun init(element: PsiElement?) {
        if (element == null) {
            super.init(element)
            return
        }

        val project = element.project

        val rootTag = TSFileUtil.getSchemaFile(project)
            .let {
                PsiManager.getInstance(project)
                    .findFile(VfsUtil.findFileByURL(it.toURL())!!) as XmlFile
            }.document?.rootTag
        super.init(rootTag)
    }

    override fun getDefaultNamespace(): String =
        TSFileUtil.getSchemaFile(this.tag.project).toString()
}