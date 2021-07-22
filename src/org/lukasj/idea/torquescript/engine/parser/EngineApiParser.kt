package org.lukasj.idea.torquescript.engine.parser

import org.lukasj.idea.torquescript.engine.model.EngineApi
import javax.xml.stream.XMLEventReader

class EngineApiParser(private val eventReader: XMLEventReader) {
    fun parse(): EngineApi {
        if (!eventReader.hasNext())
            throw Throwable("No elements in engineApi.xml")
        eventReader.nextEvent() // Start Document Event
        val xmlEvent = eventReader.nextEvent()
        return EngineApi(EngineScopeParser(eventReader, listOf()).parse(xmlEvent.asStartElement()))
    }
}

