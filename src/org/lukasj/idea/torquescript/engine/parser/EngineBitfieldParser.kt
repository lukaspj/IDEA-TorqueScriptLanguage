package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.model.EngineEnum
import org.lukasj.idea.torquescript.engine.model.EngineEnumValue
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineBitfieldParser(private val eventReader: XMLEventReader, private val scopeList: List<String>) {
    fun parse(xmlEvent: StartElement): EngineEnum {
        val name = xmlEvent.getAttributeByName(QName("", "name")).value!!
        val docs = xmlEvent.getAttributeByName(QName("", "docs")).value!!
        var values = listOf<EngineEnumValue>()

        while (eventReader.hasNext()) {
            val nextEvent = eventReader.nextEvent()
            if (nextEvent.isStartElement) {
                val nextStartEvent = nextEvent.asStartElement()
                when (nextStartEvent.name.localPart) {
                    "enums" -> {}
                    "exports" -> {}
                    "EngineEnum" -> values = values.plus(EngineEnumValueParser(eventReader, scopeList).parse(nextStartEvent))
                    else -> throw Throwable("Unexpected Engine Bitfield child ${nextStartEvent.name.localPart} ${nextStartEvent.location.lineNumber}")
                }
            }
            if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart == "EngineBitfieldType") {
                return EngineEnum(name, docs, values, scopeList)
            }
        }
        throw Throwable("No end to function")
    }
}