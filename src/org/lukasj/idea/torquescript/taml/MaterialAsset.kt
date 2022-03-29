package org.lukasj.idea.torquescript.taml

import org.lukasj.idea.torquescript.TSFileUtil
import java.nio.file.Path
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.events.StartElement

class MaterialAsset(assetFile: Path, assetName: String?)
    : TamlAsset(assetFile, assetName, "MaterialAsset") {
    var scriptFile: String? = null
    var materialDefinitionName: String? = null

    override fun parse( xmlStartElement: StartElement) {
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.lowercase() == "scriptfile" }
            ?.let { scriptFile = it.value}
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.lowercase() == "materialdefinitionname" }
            ?.let { materialDefinitionName = it.value }
    }

    override fun writeAttributes(xmlStreamWriter: XMLStreamWriter) {
        scriptFile?.let {
            xmlStreamWriter.writeAttribute("scriptFile", TSFileUtil.assetRelativePathFromFile(it, assetFile))
        }
        materialDefinitionName?.let {
            xmlStreamWriter.writeAttribute("materialDefinitionName", it)
        }
    }
}