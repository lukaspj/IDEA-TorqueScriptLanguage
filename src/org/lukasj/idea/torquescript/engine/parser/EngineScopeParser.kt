package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.model.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineScopeParser(private val eventReader: XMLEventReader, private val scopeList: Collection<String>) {
    fun parse(xmlEvent: StartElement): EngineScope {
        val name = xmlEvent.getAttributeByName(QName("", "name")).value
        val docs = xmlEvent.getAttributeByName(QName("", "docs")).value
        var functions = listOf<EngineFunction>()
        var enums = listOf<EngineEnum>()
        var classes = listOf<EngineClass>()
        var structs = listOf<EngineStruct>()
        var scopes = listOf<EngineScope>()

        while (eventReader.hasNext()) {
            val nextEvent = eventReader.nextEvent()
            if (nextEvent.isStartElement) {
                val nextStartEvent = nextEvent.asStartElement()
                when (nextStartEvent.name.localPart) {
                    "exports" -> {}
                    "EngineBitfieldType" -> enums = enums.plus(EngineBitfieldParser(eventReader, scopeList.plus(name)).parse(nextStartEvent))
                    "EngineFunction" -> functions = functions.plus(EngineFunctionParser(eventReader, scopeList.plus(name)).parse(nextStartEvent))
                    "EnginePrimitiveType" -> EnginePrimitiveParser(eventReader, scopeList.plus(name)).parse(nextStartEvent)
                    "EngineEnumType" -> enums = enums.plus(EngineEnumParser(eventReader, scopeList.plus(name)).parse(nextStartEvent))
                    "EngineClassType" -> classes = classes.plus(EngineClassParser(eventReader, scopeList.plus(name)).parse(nextStartEvent))
                    "EngineStructType" -> structs = structs.plus(EngineStructParser(eventReader, scopeList.plus(name)).parse(nextStartEvent))
                    "EngineExportScope" -> scopes = scopes.plus(EngineScopeParser(eventReader, scopeList.plus(name)).parse(nextStartEvent))
                    else -> throw Throwable("Unexpected Engine Scope child ${nextStartEvent.name.localPart} ${nextStartEvent.location.lineNumber}")
                }
            }
            if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart == "EngineExportScope") {
                return EngineScope(name, docs, functions, enums, classes, structs, scopes, scopeList)
            }
        }
        throw Throwable("No end to scope")
    }
}