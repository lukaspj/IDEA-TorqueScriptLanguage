package org.lukasj.idea.torquescript.taml

import org.lukasj.idea.torquescript.engine.EngineApiUtil
import javax.xml.stream.events.StartElement

class DeclaredAssets(
    val extension: String?,
    val path: String?,
    val recurse: Boolean?
) {
    companion object {
        fun parse(eventReader: StartElement): DeclaredAssets {
            return DeclaredAssets(
                eventReader.attributes.asSequence()
                    .firstOrNull { it.name.localPart.toLowerCase() == "extension" }
                    ?.value,
                eventReader.attributes.asSequence()
                    .firstOrNull { it.name.localPart.toLowerCase() == "path" }
                    ?.value,
                EngineApiUtil.stringToBool(
                    eventReader.attributes.asSequence()
                        .firstOrNull { it.name.localPart.toLowerCase() == "recurse" }
                        ?.value ?: "")
            )
        }
    }
}
