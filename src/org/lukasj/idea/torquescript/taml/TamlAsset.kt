package org.lukasj.idea.torquescript.taml

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.outputStream
import org.lukasj.idea.torquescript.engine.EngineApiUtil
import java.nio.file.Path
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import javax.xml.stream.events.StartElement

abstract class TamlAsset(
    val assetFile: Path,
    var assetName: String?,
    val assetType: String
) {
    var assetDescription: String? = null
    var assetCategory: String? = null
    var assetAutoUnload: Boolean? = null
    var assetInternal: Boolean? = null
    var assetPrivate: Boolean? = null

    companion object {
        fun createAssetFromType(type: String, file: VirtualFile, assetName: String?): TamlAsset? =
            when (type) {
                "ImageAsset" -> ImageAsset(Path.of(file.path), assetName)
                else -> null
            }

        fun parse(file: VirtualFile): TamlAsset? {
            val eventReader = XMLInputFactory.newInstance().createXMLEventReader(file.inputStream, "UTF-8")
            if (!eventReader.hasNext()) throw Throwable("No root element in Asset file")
            eventReader.nextEvent()
            return eventReader.nextEvent()
                .asStartElement()
                .let { startElement ->
                    createAssetFromType(
                        startElement.name.localPart,
                        file,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.toLowerCase() == "assetname" }
                            ?.value)
                        ?.also { asset ->
                            startElement.attributes.asSequence()
                                .firstOrNull { it.name.localPart.toLowerCase() == "assetdescription" }
                                ?.let { asset.assetDescription = it.value }
                            startElement.attributes.asSequence()
                                .firstOrNull { it.name.localPart.toLowerCase() == "assetcategory" }
                                ?.let { asset.assetCategory = it.value }
                            startElement.attributes.asSequence()
                                .firstOrNull { it.name.localPart.toLowerCase() == "autounload" }
                                ?.let { asset.assetAutoUnload = EngineApiUtil.stringToBool(it.value) }
                            startElement.attributes.asSequence()
                                .firstOrNull { it.name.localPart.toLowerCase() == "assetinternal" }
                                ?.let { asset.assetInternal = EngineApiUtil.stringToBool(it.value) }
                            startElement.attributes.asSequence()
                                .firstOrNull { it.name.localPart.toLowerCase() == "assetprivate" }
                                ?.let { asset.assetPrivate = EngineApiUtil.stringToBool(it.value) }
                        }
                        ?.also { it.parse(startElement) }
                }
        }
    }

    abstract fun parse(xmlStartElement: StartElement)
    abstract fun writeAttributes(xmlStreamWriter: XMLStreamWriter)
    fun saveToFile() {
        val xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(assetFile.outputStream(), "UTF-8")
        xmlStreamWriter.writeStartElement(assetType)
        xmlStreamWriter.writeAttribute("AssetName", assetName)
        assetDescription?.let {
            xmlStreamWriter.writeAttribute("AssetDescription", it)
        }
        assetCategory?.let {
            xmlStreamWriter.writeAttribute("AssetCategory", it)
        }
        assetAutoUnload?.let {
            xmlStreamWriter.writeAttribute("AssetAutoUnload", it.toString())
        }
        assetInternal?.let {
            xmlStreamWriter.writeAttribute("AssetInternal", it.toString())
        }
        assetPrivate?.let {
            xmlStreamWriter.writeAttribute("AssetPrivate", it.toString())
        }
        writeAttributes(xmlStreamWriter)
        xmlStreamWriter.writeEndElement()
        xmlStreamWriter.flush()
        xmlStreamWriter.close()
    }
}
