package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.EngineApiUtil
import org.lukasj.idea.torquescript.engine.model.EngineClass
import org.lukasj.idea.torquescript.engine.model.EngineClassProperty
import org.lukasj.idea.torquescript.engine.model.EngineFunction
import javax.xml.namespace.QName
import javax.xml.stream.XMLEventReader
import javax.xml.stream.events.StartElement

class EngineClassParser(private val eventReader: XMLEventReader, private val scopeList: List<String>) {
    fun parse(event: StartElement): EngineClass {
        val name = event.getAttributeByName(QName("", "name")).value!!
        val docs = event.getAttributeByName(QName("", "docs")).value!!
        val superType = event.getAttributeByName(QName("", "superType"))?.value
        val isAbstract = EngineApiUtil.stringToBool(event.getAttributeByName(QName("", "isAbstract")).value)
        val isInstantiable = EngineApiUtil.stringToBool(event.getAttributeByName(QName("", "isInstantiable")).value)
        val isDisposable = EngineApiUtil.stringToBool(event.getAttributeByName(QName("", "isDisposable")).value)
        val isSingleton = EngineApiUtil.stringToBool(event.getAttributeByName(QName("", "isSingleton")).value)
        var properties = listOf<EngineClassProperty>()
        var methods = listOf<EngineFunction>()


        while (eventReader.hasNext()) {
            val nextEvent = eventReader.nextEvent()
            if (nextEvent.isStartElement) {
                val nextStartEvent = nextEvent.asStartElement()
                when (nextStartEvent.name.localPart) {
                    "properties" -> {} // Ignore
                    "EnginePropertyGroup" -> {} // Ignore
                    "exports" -> {} // Ignore
                    "EngineProperty" -> properties = properties.plus(
                        EngineClassPropertyParser(
                            eventReader,
                            scopeList
                        ).parse(nextStartEvent)
                    )
                    "EngineFunction" -> methods = methods.plus(EngineFunctionParser(eventReader, scopeList.plus(name)).parse(nextStartEvent))
                    else -> throw Throwable("Unexpected Engine Function child")
                }
            }
            if (nextEvent.isEndElement && nextEvent.asEndElement().name.localPart == "EngineClassType") {
                return EngineClass(
                    name,
                    docs,
                    superType,
                    isAbstract,
                    isInstantiable,
                    isDisposable,
                    isSingleton,
                    properties,
                    methods,
                    scopeList
                )
            }
        }
        throw Throwable("No end to function")
    }
}