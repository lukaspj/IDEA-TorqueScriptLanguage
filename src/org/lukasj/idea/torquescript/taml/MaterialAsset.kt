package org.lukasj.idea.torquescript.taml

import org.lukasj.idea.torquescript.TSFileUtil
import java.nio.file.Path
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.events.StartElement

class MaterialAsset(assetFile: Path, assetName: String?) : TamlAsset(assetFile, assetName, "MaterialAsset") {
    var mapTo: String? = null
    var materialDefinitionName: String? = null

    override fun parse(xmlStartElement: StartElement) {
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.lowercase() == "materialdefinitionname" }
            ?.let { materialDefinitionName = it.value }
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.lowercase() == "scriptfile" }
            ?.let { mapTo = it.value }
    }

    override fun parseChild(element: StartElement) {
        if (element.name.localPart.lowercase() == "material") {
            element.attributes.asSequence()
                .firstOrNull { it.name.localPart.lowercase() == "mapto" }
                ?.let { mapTo = it.value }
        }
    }

    override fun writeAttributes(xmlStreamWriter: XMLStreamWriter) {
        materialDefinitionName?.let {
            xmlStreamWriter.writeAttribute("materialDefinitionName", it)
        }

        xmlStreamWriter.writeStartElement("Material")
        xmlStreamWriter.writeAttribute("mapTo", mapTo)
        xmlStreamWriter.writeAttribute("Name", materialDefinitionName)
        xmlStreamWriter.writeEndElement()
    }
}