package org.lukasj.idea.torquescript.taml

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.createDirectories
import org.lukasj.idea.torquescript.engine.EngineApiUtil
import org.lukasj.idea.torquescript.taml.xml.IndentingXMLStreamWriter
import java.nio.file.Files
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
                "MaterialAsset" -> MaterialAsset(Path.of(file.path), assetName)
                "ShapeAsset" -> ShapeAsset(Path.of(file.path), assetName)
                else -> {
                    logger<TamlAsset>()
                        .info("No parser for asset type $type was not implemented")
                    null
                }
            }

        fun parse(file: VirtualFile): TamlAsset? {
            val eventReader = XMLInputFactory.newInstance().createXMLEventReader(file.inputStream, "UTF-8")
            if (!eventReader.hasNext()) throw Throwable("No root element in Asset file")
            eventReader.nextEvent()
            try {
                return eventReader.nextEvent()
                    .asStartElement()
                    .let { startElement ->
                        createAssetFromType(
                            startElement.name.localPart,
                            file,
                            startElement.attributes.asSequence()
                                .firstOrNull { it.name.localPart.lowercase() == "assetname" }
                                ?.value)
                            ?.also { asset ->
                                startElement.attributes.asSequence()
                                    .firstOrNull { it.name.localPart.lowercase() == "assetdescription" }
                                    ?.let { asset.assetDescription = it.value }
                                startElement.attributes.asSequence()
                                    .firstOrNull { it.name.localPart.lowercase() == "assetcategory" }
                                    ?.let { asset.assetCategory = it.value }
                                startElement.attributes.asSequence()
                                    .firstOrNull { it.name.localPart.lowercase() == "autounload" }
                                    ?.let { asset.assetAutoUnload = EngineApiUtil.stringToBool(it.value) }
                                startElement.attributes.asSequence()
                                    .firstOrNull { it.name.localPart.lowercase() == "assetinternal" }
                                    ?.let { asset.assetInternal = EngineApiUtil.stringToBool(it.value) }
                                startElement.attributes.asSequence()
                                    .firstOrNull { it.name.localPart.lowercase() == "assetprivate" }
                                    ?.let { asset.assetPrivate = EngineApiUtil.stringToBool(it.value) }
                            }
                            ?.also { it.parse(startElement) }
                    }
            } catch (ex: Exception) {
                throw Exception("Parsing of ${file.name} failed", ex)
            }
        }
    }

    abstract fun parse(xmlStartElement: StartElement)
    abstract fun writeAttributes(xmlStreamWriter: XMLStreamWriter)
    fun saveToFile() {
        // Compatibilty fix for IDEA Ultimate 2021.1.3
        // For some reason this doesn't work:
        // val outputStream = assetFile.outputStream()
        // Probably a JDK version thing or something
        assetFile.parent?.createDirectories()
        val outputStream = Files.newOutputStream(assetFile)

        val xmlStreamWriter = object :
            IndentingXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8")) {

            private var indent = 0

            override fun writeStartElement(localName: String) {
                this.indent++
                super.writeStartElement(localName)
            }

            override fun writeStartElement(namespaceURI: String, localName: String) {
                this.indent++
                super.writeStartElement(namespaceURI, localName)
            }

            override fun writeStartElement(prefix: String, localName: String, namespaceURI: String) {
                this.indent++
                super.writeStartElement(prefix, localName, namespaceURI)
            }

            override fun writeEndElement() {
                this.indent--
                super.writeEndElement()
            }

            fun onWriteAttribute() {
                flush()
                outputStream.write("\n".toByteArray())
                for (i in 1..indent) {
                    outputStream.write("  ".toByteArray())
                }
            }

            override fun writeAttribute(localName: String, value: String) {
                onWriteAttribute()
                super.writeAttribute(localName, value)
            }

            override fun writeAttribute(prefix: String, namespaceURI: String, localName: String, value: String) {
                onWriteAttribute()
                super.writeAttribute(prefix, namespaceURI, localName, value)
            }

            override fun writeAttribute(namespaceURI: String, localName: String, value: String) {
                onWriteAttribute()
                super.writeAttribute(namespaceURI, localName, value)
            }
        }

        xmlStreamWriter.writeStartElement(assetType)
        xmlStreamWriter.writeAttribute("AssetName", assetName ?: "")
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

        VfsUtil.findFile(assetFile.parent.toAbsolutePath(), true)?.refresh(false, false)
    }
}
