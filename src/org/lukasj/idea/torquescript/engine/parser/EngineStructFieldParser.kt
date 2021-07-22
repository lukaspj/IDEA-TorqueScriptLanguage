package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.model.EngineStructField
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineStructFieldParser(eventReader: XMLEventReader, scopeList: List<String>) {
    fun parse(event: StartElement): EngineStructField {
        val name = event.getAttributeByName(QName("", "name")).value!!
        val docs = event.getAttributeByName(QName("", "docs")).value!!
        val typeName = event.getAttributeByName(QName("", "type")).value!!
        val indexedSize = event.getAttributeByName(QName("", "indexedSize")).value!!
        val offset = event.getAttributeByName(QName("", "offset")).value!!
        return EngineStructField(name, docs, typeName, indexedSize, offset)
    }
}
