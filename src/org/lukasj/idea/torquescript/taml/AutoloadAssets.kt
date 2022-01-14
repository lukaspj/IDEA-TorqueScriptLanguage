package org.lukasj.idea.torquescript.taml

import org.lukasj.idea.torquescript.engine.EngineApiUtil
import javax.xml.stream.events.StartElement

class AutoloadAssets(
    val assetType: String?,
    val path: String?,
    val recurse: Boolean?
) {
    companion object {
        fun parse(eventReader: StartElement): AutoloadAssets {
            return AutoloadAssets(
                eventReader.attributes.asSequence()
                    .firstOrNull { it.name.localPart.toLowerCase() == "assettype" }
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
