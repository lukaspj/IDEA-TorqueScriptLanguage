package org.lukasj.idea.torquescript.taml

import com.intellij.openapi.vfs.VirtualFile
import javax.xml.stream.XMLInputFactory

class TamlAsset(
    val file: VirtualFile,
    val assetName: String?,
    val assetType: String
) {
    companion object {
        fun parse(file: VirtualFile): TamlAsset? {
            val eventReader = XMLInputFactory.newInstance().createXMLEventReader(file.inputStream, "UTF-8")
            if (!eventReader.hasNext()) throw Throwable("No root element in Asset file")
            eventReader.nextEvent()
            return eventReader.nextEvent()
                .asStartElement()
                .let { startElement ->
                    return TamlAsset(
                        file,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.toLowerCase() == "assetname" }
                            ?.value,
                        startElement.name.localPart
                    )
                }
        }
    }
}
