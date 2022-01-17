package org.lukasj.idea.torquescript.taml

import org.lukasj.idea.torquescript.engine.EngineApiUtil
import java.nio.file.Path
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.events.StartElement

class ImageAsset(assetFile: Path, assetName: String?)
    : TamlAsset(assetFile, assetName, "ImageAsset") {
    var imageFilePath: String? = null
    var imageType: String? = null
    var isHdrImage: Boolean? = null
    var useMips: Boolean? = null

    override fun parse( xmlStartElement: StartElement) {
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.toLowerCase() == "imagefile" }
            ?.let { imageFilePath = it.value}
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.toLowerCase() == "imagetype" }
            ?.let { imageType = it.value }
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.toLowerCase() == "ishdrimage" }
            ?.let { isHdrImage = EngineApiUtil.stringToBool(it.value) }
        xmlStartElement.attributes.asSequence()
            .firstOrNull { it.name.localPart.toLowerCase() == "usemips" }
            ?.let { useMips = EngineApiUtil.stringToBool(it.value) }
    }

    override fun writeAttributes(xmlStreamWriter: XMLStreamWriter) {
        imageFilePath?.let {
            xmlStreamWriter.writeAttribute("imageFile", it)
        }
        imageType?.let {
            xmlStreamWriter.writeAttribute("imageType", it)
        }
        isHdrImage?.let {
            xmlStreamWriter.writeAttribute("isHdrImage", it.toString())
        }
        useMips?.let {
            xmlStreamWriter.writeAttribute("useMips", it.toString())
        }
    }
}