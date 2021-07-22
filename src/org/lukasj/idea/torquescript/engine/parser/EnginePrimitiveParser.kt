package org.lukasj.idea.torquescript.engine.parser

import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EnginePrimitiveParser(private val eventReader: XMLEventReader, private val scopeList: List<String>) {
    fun parse(xmlEvent: StartElement): Unit {
        while (eventReader.hasNext()) {
            val nextEvent = eventReader.nextEvent()
            if (nextEvent.isStartElement) {
                val nextStartEvent = nextEvent.asStartElement()
                when (nextStartEvent.name.localPart) {
                    "exports" -> {}
                    else -> throw Throwable("Unexpected Engine Primitive child ${nextStartEvent.name.localPart} ${nextStartEvent.location.lineNumber}")
                }
            }
            if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart == "EnginePrimitiveType") {
                return
            }
        }
        throw Throwable("No end to function")
    }
}