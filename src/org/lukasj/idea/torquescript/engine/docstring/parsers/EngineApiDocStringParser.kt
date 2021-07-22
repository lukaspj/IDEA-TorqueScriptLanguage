package org.lukasj.idea.torquescript.engine.docstring.parsers

import org.lukasj.idea.torquescript.engine.docstring.elements.*

class EngineApiDocStringParser {
    fun parse(docs: String): IDocElement {
        val docTags = docs.split(Regex("(?=@)"))
            .map { DocTag(it) }

        docTags.forEachIndexed { idx, tag ->
            if (idx == 0) {
                return@forEachIndexed
            }

            docTags[idx - 1].next = tag
            tag.prev = docTags[idx - 1]
        }

        return CompoundDocElement()
            .also {
                it.children =
                    docTags.filter { !parseDocTag(it).isLeaf }
                        .map { docTag ->
                            val element = parseDocTag(docTag)

                            if (docTag.next == null || !parseDocTag(docTag.next!!).isLeaf) {
                                return@map element
                            }

                            var nextTag = docTag.next!!
                            var nextElement = parseDocTag(docTag.next!!)
                            while (nextElement.isLeaf) {
                                when (nextElement) {
                                    is ParameterRefDocElement ->
                                        element.children += listOf(
                                            nextElement,
                                            TextDocElement(nextElement.remainder)
                                        )
                                    is TextDocElement ->
                                        element.children += nextElement
                                    is NullDocElement -> {
                                    }
                                    is InternalDocElement -> {
                                    }
                                    else -> throw Throwable("Unknown element type for EngineApiDocString: $nextElement")
                                }

                                if (nextTag.next == null) {
                                    break
                                }
                                nextTag = nextTag.next!!
                                nextElement = parseDocTag(nextTag)
                            }

                            return@map element
                        }
            }
    }

    fun parseDocTag(tag: DocTag) =
        PARSERS.first { it.matchesTag(tag) }
            .parse(tag)

    companion object {
        val PARSERS = listOf(
            DescriptionStringParser(),
            SummaryStringParser(),
            RemarkStringParser(),
            CodeExampleStringParser(),
            ParameterStringParser(),
            ReturnStringParser(),
            InGroupStringParser(),
            SeeStringParser(),
            InternalStringParser(),
            ParameterRefStringParser(),
            InlineStringParser(),
            NullStringParser(),
        )
    }
}