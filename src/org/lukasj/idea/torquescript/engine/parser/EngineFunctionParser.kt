package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.EngineApiUtil
import org.lukasj.idea.torquescript.engine.model.EngineFunction
import org.lukasj.idea.torquescript.engine.model.EngineFunctionArgument
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineFunctionParser(private val eventReader: XMLEventReader, private val scopeList: List<String>) {
    fun parse(xmlEvent: StartElement): EngineFunction {
        val name = xmlEvent.getAttributeByName(QName("", "name")).value!!
        val docs = xmlEvent.getAttributeByName(QName("", "docs")).value!!
        val returnType = xmlEvent.getAttributeByName(QName("", "returnType")).value!!
        val symbol = xmlEvent.getAttributeByName(QName("", "symbol")).value!!
        val isCallback = EngineApiUtil.stringToBool(xmlEvent.getAttributeByName(QName("", "isCallback")).value)
        val isVariadic = EngineApiUtil.stringToBool(xmlEvent.getAttributeByName(QName("", "isVariadic")).value)
        var arguments = listOf<EngineFunctionArgument>()


        while (eventReader.hasNext()) {
            val nextEvent = eventReader.nextEvent()
            if (nextEvent.isStartElement) {
                val nextStartEvent = nextEvent.asStartElement()
                when (nextStartEvent.name.localPart) {
                    "arguments" -> {}
                    "EngineFunctionArgument" -> arguments = arguments.plus(
                        EngineFunctionArgumentParser(
                            eventReader,
                            scopeList
                        ).parse(nextStartEvent))
                    else -> throw Throwable("Unexpected Engine Function child ${nextStartEvent.name.localPart}")
                }
            }
            if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart == "EngineFunction") {
                return EngineFunction(name, docs, returnType, symbol, isCallback, isVariadic, arguments, scopeList)
            }
        }
        throw Throwable("No end to function")
    }
}