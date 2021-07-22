package org.lukasj.idea.torquescript.engine.docstring.elements

class DocTag(private val string: String) {
    val tag: String
    val text: String
    var next: DocTag?
    var prev: DocTag?

    init {
        if (string.indexOf(' ') < 0 || !string.trim().startsWith("@")) {
            if (string.startsWith("@")) {
                tag = string
                text = "";
            } else {
                tag = "";
                text = string
            }
        } else {
            tag = string.substring(0, string.indexOfAny(charArrayOf(' ', '\n')))
            text = string.substring(string.indexOfAny(charArrayOf(' ', '\n')))
        }
        next = null
        prev = null
    }
}