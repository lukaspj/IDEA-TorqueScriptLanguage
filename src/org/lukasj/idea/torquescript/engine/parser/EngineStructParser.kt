package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.model.EngineStruct
import org.lukasj.idea.torquescript.engine.model.EngineStructField
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineStructParser(private val eventReader: XMLEventReader, private val scopeList: List<String>) {
    fun parse(event: StartElement): EngineStruct {
        val name = event.getAttributeByName(QName("", "name")).value
        val docs = event.getAttributeByName(QName("", "docs")).value

        var fields = listOf<EngineStructField>()

        while (eventReader.hasNext()) {
            val nextEvent = eventReader.nextEvent()
            if (nextEvent.isStartElement) {
                val nextStartEvent = nextEvent.asStartElement()
                when (nextStartEvent.name.localPart) {
                    "fields" -> {}
                    "exports" -> {}
                    "EngineField" -> fields = fields.plus(
                        EngineStructFieldParser(
                            eventReader,
                            scopeList
                        ).parse(nextStartEvent))
                    else -> throw Throwable("Unexpected Engine Struct child")
                }
            }
            if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart == "EngineStructType") {
                return EngineStruct(name, docs, fields, scopeList)
            }
        }
        throw Throwable("No end to struct")
    }
}
