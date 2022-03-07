package org.lukasj.idea.torquescript.taml.xml

import java.util.*
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamWriter

open class IndentingXMLStreamWriter(writer: XMLStreamWriter) :
    DelegatingXMLStreamWriter(writer) {
    private var state: Any?
    private val stateStack: Stack<Any?>
    private var indentStep: String
    private var depth: Int

    init {
        state = SEEN_NOTHING
        stateStack = Stack()
        indentStep = "  "
        depth = 0
    }

    fun getIndentStep(): Int {
        return indentStep.length
    }

    fun setIndentStep(indentStep: Int) {
        var currentIndentStep = indentStep
        val s = StringBuilder()
        while (currentIndentStep > 0) {
            s.append(' ')
            --currentIndentStep
        }
        this.setIndentStep(s.toString())
    }

    fun setIndentStep(s: String) {
        indentStep = s
    }

    @Throws(XMLStreamException::class)
    open fun onStartElement() {
        stateStack.push(SEEN_ELEMENT)
        state = SEEN_NOTHING
        if (depth > 0) {
            super.writeCharacters("\n")
        }
        doIndent()
        ++depth
    }

    @Throws(XMLStreamException::class)
    open fun onEndElement() {
        --depth
        if (state === SEEN_ELEMENT) {
            super.writeCharacters("\n")
            doIndent()
        }
        state = stateStack.pop()
    }

    @Throws(XMLStreamException::class)
    open fun onEmptyElement() {
        state = SEEN_ELEMENT
        if (depth > 0) {
            super.writeCharacters("\n")
        }
        doIndent()
    }

    @Throws(XMLStreamException::class)
    open fun doIndent() {
        if (depth > 0) {
            for (i in 0 until depth) {
                super.writeCharacters(indentStep)
            }
        }
    }

    @Throws(XMLStreamException::class)
    override fun writeStartDocument() {
        super.writeStartDocument()
        super.writeCharacters("\n")
    }

    @Throws(XMLStreamException::class)
    override fun writeStartDocument(version: String) {
        super.writeStartDocument(version)
        super.writeCharacters("\n")
    }

    @Throws(XMLStreamException::class)
    override fun writeStartDocument(encoding: String, version: String) {
        super.writeStartDocument(encoding, version)
        super.writeCharacters("\n")
    }

    @Throws(XMLStreamException::class)
    override fun writeStartElement(localName: String) {
        onStartElement()
        super.writeStartElement(localName)
    }

    @Throws(XMLStreamException::class)
    override fun writeStartElement(namespaceURI: String, localName: String) {
        onStartElement()
        super.writeStartElement(namespaceURI, localName)
    }

    @Throws(XMLStreamException::class)
    override fun writeStartElement(prefix: String, localName: String, namespaceURI: String) {
        onStartElement()
        super.writeStartElement(prefix, localName, namespaceURI)
    }

    @Throws(XMLStreamException::class)
    override fun writeEmptyElement(namespaceURI: String, localName: String) {
        onEmptyElement()
        super.writeEmptyElement(namespaceURI, localName)
    }

    @Throws(XMLStreamException::class)
    override fun writeEmptyElement(prefix: String, localName: String, namespaceURI: String) {
        onEmptyElement()
        super.writeEmptyElement(prefix, localName, namespaceURI)
    }

    @Throws(XMLStreamException::class)
    override fun writeEmptyElement(localName: String) {
        onEmptyElement()
        super.writeEmptyElement(localName)
    }

    @Throws(XMLStreamException::class)
    override fun writeEndElement() {
        onEndElement()
        super.writeEndElement()
    }

    @Throws(XMLStreamException::class)
    override fun writeCharacters(text: String) {
        state = SEEN_DATA
        super.writeCharacters(text)
    }

    @Throws(XMLStreamException::class)
    override fun writeCharacters(text: CharArray, start: Int, len: Int) {
        state = SEEN_DATA
        super.writeCharacters(text, start, len)
    }

    @Throws(XMLStreamException::class)
    override fun writeCData(data: String) {
        state = SEEN_DATA
        super.writeCData(data)
    }

    companion object {
        private val SEEN_NOTHING = Any()
        private val SEEN_ELEMENT = Any()
        private val SEEN_DATA = Any()
    }
}