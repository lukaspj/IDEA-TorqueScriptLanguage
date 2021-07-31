package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.model.*
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineScopeParser(private val eventReader: XMLEventReader, private val scopeList: List<String>) {
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
                val childScopeList =
                    if(name.isNotEmpty()) {
                        scopeList.plus(name)
                    } else {
                        scopeList
                    }
                when (nextStartEvent.name.localPart) {
                    "exports" -> {}
                    "EngineBitfieldType" -> enums = enums.plus(EngineBitfieldParser(eventReader, childScopeList).parse(nextStartEvent))
                    "EngineFunction" -> functions = functions.plus(EngineFunctionParser(eventReader, childScopeList).parse(nextStartEvent))
                    "EnginePrimitiveType" -> EnginePrimitiveParser(eventReader, childScopeList).parse(nextStartEvent)
                    "EngineEnumType" -> enums = enums.plus(EngineEnumParser(eventReader, childScopeList).parse(nextStartEvent))
                    "EngineClassType" -> classes = classes.plus(EngineClassParser(eventReader, childScopeList).parse(nextStartEvent))
                    "EngineStructType" -> structs = structs.plus(EngineStructParser(eventReader, childScopeList).parse(nextStartEvent))
                    "EngineExportScope" -> scopes = scopes.plus(EngineScopeParser(eventReader, childScopeList).parse(nextStartEvent))
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