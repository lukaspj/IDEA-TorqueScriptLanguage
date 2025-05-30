package org.lukasj.idea.torquescript.taml

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import javax.xml.stream.XMLInputFactory

class TamlModule(
    val project: Project,
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
        fun parse(project: Project, file: VirtualFile): TamlModule {
            val eventReader = XMLInputFactory.newInstance().createXMLEventReader(file.inputStream, "UTF-8")
            if (!eventReader.hasNext()) throw Throwable("No root element in Module file")
            eventReader.nextEvent()
            eventReader.nextEvent()
                .asStartElement()
                .let { startElement ->
                    val declaredAssets = mutableListOf<DeclaredAssets>()
                    val autoloadAssets = mutableListOf<AutoloadAssets>()
                    while (eventReader.hasNext()) {
                        val nextEvent = eventReader.nextEvent()
                        if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart == "ModuleDefinition") break
                        if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart in listOf("ModuleDefinition", "DeclaredAssets", "AutoloadAssets")) break
                        if (nextEvent.isCharacters) continue
                        if (!nextEvent.isStartElement) {
                            println("Unexpected Module Definition child in ${file.name} line number: ${nextEvent.location.lineNumber}")
                            continue
                        }
                        when (nextEvent.asStartElement().name.localPart) {
                            "DeclaredAssets" -> declaredAssets += DeclaredAssets.parse(nextEvent.asStartElement())
                            "AutoloadAssets" -> autoloadAssets += AutoloadAssets.parse(nextEvent.asStartElement())
                            else -> throw Throwable("Unexpected Module Definition child ${nextEvent.asStartElement().name.localPart} ${nextEvent.location.lineNumber}")
                        }
                    }
                    return TamlModule(
                        project,
                        file,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.lowercase() == "moduleid" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.lowercase() == "versionid" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.lowercase() == "group" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.lowercase() == "scriptfile" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.lowercase() == "createfunction" }
                            ?.value,
                        startElement.attributes.asSequence()
                            .firstOrNull { it.name.localPart.lowercase() == "destroyfunction" }
                            ?.value,
                        declaredAssets,
                        autoloadAssets
                    )
                }
        }
    }
}