package org.lukasj.idea.torquescript.taml

import org.lukasj.idea.torquescript.TSFileUtil
import java.nio.file.Path
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.events.StartElement

class ShapeAsset(assetFile: Path, assetName: String?)
    : TamlAsset(assetFile, assetName, "ShapeAsset") {
    var fileName: String? = null
    var constructorFileName: String? = null
    var diffuseImposterFileName: String? = null
    var normalImposterFileName: String? = null
    var materialSlots = mutableMapOf<Int, String>()

    override fun parse( xmlStartElement: StartElement) {
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.toLowerCase() == "filename" }
            ?.let { fileName = it.value}
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.toLowerCase() == "constuctorFileName" }
            ?.let { constructorFileName = it.value }
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.toLowerCase() == "diffuseimposterfilename" }
            ?.let { diffuseImposterFileName = it.value }
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.toLowerCase() == "normalimposterfilename" }
            ?.let { normalImposterFileName = it.value }

        xmlStartElement.attributes.asSequence()
            .map { Pair(it, Regex("materialslot([0-9]+)").find(it.name.localPart.toLowerCase())) }
            .filter { it.second != null }
            .forEach {
                materialSlots[it.second!!.groupValues[1].toInt()] = it.first.value
            }
    }

    override fun writeAttributes(xmlStreamWriter: XMLStreamWriter) {
        fileName?.let {
            xmlStreamWriter.writeAttribute("fileName", TSFileUtil.assetRelativePathFromFile(it, assetFile))
        }
        constructorFileName?.let {
            xmlStreamWriter.writeAttribute("constuctorFileName", TSFileUtil.assetRelativePathFromFile(it, assetFile))
        }
        diffuseImposterFileName?.let {
            xmlStreamWriter.writeAttribute("diffuseImposterFileName", TSFileUtil.assetRelativePathFromFile(it, assetFile))
        }
        normalImposterFileName?.let {
            xmlStreamWriter.writeAttribute("normalImposterFileName", TSFileUtil.assetRelativePathFromFile(it, assetFile))
        }
        materialSlots.forEach {
            xmlStreamWriter.writeAttribute("materialSlots${it.key}", TSFileUtil.assetRelativePathFromFile(it.value, assetFile))
        }
    }
}