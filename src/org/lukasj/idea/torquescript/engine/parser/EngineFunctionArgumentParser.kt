package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.model.EngineFunctionArgument
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineFunctionArgumentParser(eventReader: XMLEventReader, scopeList: List<String>) {
    fun parse(nextEvent: StartElement): EngineFunctionArgument {
        val name = nextEvent.getAttributeByName(QName("", "name")).value!!
        val typeName = nextEvent.getAttributeByName(QName("", "type")).value!!
        val defaultValue = nextEvent.getAttributeByName(QName("", "defaultValue"))?.value

        return EngineFunctionArgument(name, typeName, defaultValue)
    }
}