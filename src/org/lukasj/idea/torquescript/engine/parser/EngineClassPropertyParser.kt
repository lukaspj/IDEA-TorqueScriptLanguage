package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.EngineApiUtil
import org.lukasj.idea.torquescript.engine.model.EngineClassProperty
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineClassPropertyParser(eventReader: XMLEventReader, scopeList: List<String>) {
    fun parse(event: StartElement): EngineClassProperty {
        val name = event.getAttributeByName(QName("", "name")).value!!
        val docs = event.getAttributeByName(QName("", "docs")).value!!
        val typeName = event.getAttributeByName(QName("", "type")).value!!
        val indexedSize = event.getAttributeByName(QName("", "indexedSize")).value.toInt()
        val isConstant = EngineApiUtil.stringToBool(event.getAttributeByName(QName("", "isConstant")).value)
        val isTransient = EngineApiUtil.stringToBool(event.getAttributeByName(QName("", "isTransient")).value)
        val isVisible = EngineApiUtil.stringToBool(event.getAttributeByName(QName("", "isVisible")).value)
        return EngineClassProperty(name, docs, typeName, indexedSize, isConstant, isTransient, isVisible)
    }
}
