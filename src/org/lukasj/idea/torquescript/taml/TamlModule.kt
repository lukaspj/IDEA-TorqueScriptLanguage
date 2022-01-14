package org.lukasj.idea.torquescript.taml

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.stopAfter
import kotlinx.coroutines.flow.asFlow
import javax.xml.namespace.QName
import javax.xml.stream.XMLInputFactory

class TamlModule(
    val file: VirtualFile,
    val moduleId: String?,
    val versionId: String?,
    val group: String?,
    val scriptFile: String?,
    val createFunction: String?,
    val destroyFunction: String?,
    val declaredAssetDeclarations: List<DeclaredAssets>,
    val autoloadAssetDeclarations: List<AutoloadAssets>
) {
    companion object {
        fun parse(file: VirtualFile): TamlModule? {
            val eventReader = XMLInputFactory.newInstance().createXMLEventReader(file.inputStream, "UTF-8")
            if (!eventReader.hasNext()) throw Throwable("No root element in Module file")
            eventReader.nextEvent()
            return eventReader.nextEvent()
                .asStartElement()
                .let { startElement ->
                    val declaredAssets = mutableListOf<DeclaredAssets>()
                    val autoloadAssets = mutableListOf<AutoloadAssets>()
                    while (eventReader.hasNext()) {
                        val nextEvent = eventReader.nextEvent()
                        if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart == "ModuleDefinition") break
                        if (nextEvent.isCharacters) continue
                        if (!nextEvent.isStartElement) {
                            println("Unexpected Module Definition child ${nextEvent.toString()} ${nextEvent.location.lineNumber}")
                            continue
                        }
                        when (nextEvent.asStartElement().name.localPart) {
                            "DeclaredAssets" -> declaredAssets += DeclaredAssets.parse(nextEvent.asStartElement())
                            "AutoloadAssets" -> autoloadAssets += AutoloadAssets.parse(nextEvent.asStartElement())
                            else -> throw Throwable("Unexpected Module Definition child ${nextEvent.asStartElement().name.localPart} ${nextEvent.location.lineNumber}")
                        }
                    }
                    return TamlModule(
                        file,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.toLowerCase() == "moduleid" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.toLowerCase() == "versionid" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.toLowerCase() == "group" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.toLowerCase() == "scriptfile" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.toLowerCase() == "createfunction" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.toLowerCase() == "destroyfunction" }
                            ?.value,
                        declaredAssets,
                        autoloadAssets
                    )
                }
        }
    }
}