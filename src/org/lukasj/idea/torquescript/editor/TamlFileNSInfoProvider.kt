package org.lukasj.idea.torquescript.editor

import com.intellij.psi.PsiElement
import com.intellij.psi.filters.ElementFilter
import com.intellij.psi.meta.MetaDataContributor
import com.intellij.psi.meta.MetaDataRegistrar
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlFileNSInfoProvider
import org.lukasj.idea.torquescript.TSFileUtil


class TamlFileNSInfoProvider : XmlFileNSInfoProvider, MetaDataContributor {
    override fun getDefaultNamespaces(file: XmlFile): Array<Array<String>> =
        arrayOf(arrayOf(TSFileUtil.getSchemaFile(file.project).toString()))

    override fun overrideNamespaceFromDocType(file: XmlFile) =
        false

    override fun contributeMetaData(registrar: MetaDataRegistrar) =
        registrar.registerMetaData(object : ElementFilter {
            override fun isAcceptable(element: Any?, context: PsiElement?) =
                element is XmlElement
                        // XML files are converted to a dtd file and then that is what we are working on, so
                        // we can't detect them as TAML files.. Best we can do atm is to check on extension
                        && (element.containingFile.name.endsWith(".taml.dtd") || element.containingFile.name.endsWith(".module.dtd"))

            override fun isClassAcceptable(hintClass: Class<*>) =
                true

        }, TamlNamespaceDescriptor::class.java)
}