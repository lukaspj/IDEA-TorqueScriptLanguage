package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.model.EngineEnumValue
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineEnumValueParser(eventReader: XMLEventReader, scopeList: List<String>) {
    fun parse(nextEvent: StartElement): EngineEnumValue {
        val name = nextEvent.getAttributeByName(QName("", "name")).value!!
        val docs = nextEvent.getAttributeByName(QName("", "docs")).value!!
        val value = nextEvent.getAttributeByName(QName("", "value")).value!!
        return EngineEnumValue(name, docs, value)
    }
}