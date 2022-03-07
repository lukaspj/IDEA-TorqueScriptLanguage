package org.lukasj.idea.torquescript.taml.xml

import kotlin.Throws
import java.lang.IllegalArgumentException
import javax.xml.namespace.NamespaceContext
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter

abstract class DelegatingXMLStreamWriter(private val writer: XMLStreamWriter) : XMLStreamWriter {
    @Throws(XMLStreamException::class)
    override fun writeStartElement(localName: String) {
        writer.writeStartElement(localName)
    }

    @Throws(XMLStreamException::class)
    override fun writeStartElement(namespaceURI: String, localName: String) {
        writer.writeStartElement(namespaceURI, localName)
    }

    @Throws(XMLStreamException::class)
    override fun writeStartElement(prefix: String, localName: String, namespaceURI: String) {
        writer.writeStartElement(prefix, localName, namespaceURI)
    }

    @Throws(XMLStreamException::class)
    override fun writeEmptyElement(namespaceURI: String, localName: String) {
        writer.writeEmptyElement(namespaceURI, localName)
    }

    @Throws(XMLStreamException::class)
    override fun writeEmptyElement(prefix: String, localName: String, namespaceURI: String) {
        writer.writeEmptyElement(prefix, localName, namespaceURI)
    }

    @Throws(XMLStreamException::class)
    override fun writeEmptyElement(localName: String) {
        writer.writeEmptyElement(localName)
    }

    @Throws(XMLStreamException::class)
    override fun writeEndElement() {
        writer.writeEndElement()
    }

    @Throws(XMLStreamException::class)
    override fun writeEndDocument() {
        writer.writeEndDocument()
    }

    @Throws(XMLStreamException::class)
    override fun close() {
        writer.close()
    }

    @Throws(XMLStreamException::class)
    override fun flush() {
        writer.flush()
    }

    @Throws(XMLStreamException::class)
    override fun writeAttribute(localName: String, value: String) {
        writer.writeAttribute(localName, value)
    }

    @Throws(XMLStreamException::class)
    override fun writeAttribute(prefix: String, namespaceURI: String, localName: String, value: String) {
        writer.writeAttribute(prefix, namespaceURI, localName, value)
    }

    @Throws(XMLStreamException::class)
    override fun writeAttribute(namespaceURI: String, localName: String, value: String) {
        writer.writeAttribute(namespaceURI, localName, value)
    }

    @Throws(XMLStreamException::class)
    override fun writeNamespace(prefix: String, namespaceURI: String) {
        writer.writeNamespace(prefix, namespaceURI)
    }

    @Throws(XMLStreamException::class)
    override fun writeDefaultNamespace(namespaceURI: String) {
        writer.writeDefaultNamespace(namespaceURI)
    }

    @Throws(XMLStreamException::class)
    override fun writeComment(data: String) {
        writer.writeComment(data)
    }

    @Throws(XMLStreamException::class)
    override fun writeProcessingInstruction(target: String) {
        writer.writeProcessingInstruction(target)
    }

    @Throws(XMLStreamException::class)
    override fun writeProcessingInstruction(target: String, data: String) {
        writer.writeProcessingInstruction(target, data)
    }

    @Throws(XMLStreamException::class)
    override fun writeCData(data: String) {
        writer.writeCData(data)
    }

    @Throws(XMLStreamException::class)
    override fun writeDTD(dtd: String) {
        writer.writeDTD(dtd)
    }

    @Throws(XMLStreamException::class)
    override fun writeEntityRef(name: String) {
        writer.writeEntityRef(name)
    }

    @Throws(XMLStreamException::class)
    override fun writeStartDocument() {
        writer.writeStartDocument()
    }

    @Throws(XMLStreamException::class)
    override fun writeStartDocument(version: String) {
        writer.writeStartDocument(version)
    }

    @Throws(XMLStreamException::class)
    override fun writeStartDocument(encoding: String, version: String) {
        writer.writeStartDocument(encoding, version)
    }

    @Throws(XMLStreamException::class)
    override fun writeCharacters(text: String) {
        writer.writeCharacters(text)
    }

    @Throws(XMLStreamException::class)
    override fun writeCharacters(text: CharArray, start: Int, len: Int) {
        writer.writeCharacters(text, start, len)
    }

    @Throws(XMLStreamException::class)
    override fun getPrefix(uri: String): String {
        return writer.getPrefix(uri)
    }

    @Throws(XMLStreamException::class)
    override fun setPrefix(prefix: String, uri: String) {
        writer.setPrefix(prefix, uri)
    }

    @Throws(XMLStreamException::class)
    override fun setDefaultNamespace(uri: String) {
        writer.setDefaultNamespace(uri)
    }

    @Throws(XMLStreamException::class)
    override fun setNamespaceContext(context: NamespaceContext?) {
        writer.namespaceContext = context
    }

    override fun getNamespaceContext(): NamespaceContext {
        return writer.namespaceContext
    }

    @Throws(IllegalArgumentException::class)
    override fun getProperty(name: String): Any {
        return writer.getProperty(name)
    }
}