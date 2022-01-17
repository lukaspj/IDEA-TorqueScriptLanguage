package org.lukasj.idea.torquescript.engine.model

class EngineEnumValue(
    val name: String,
    val docs: String,
    val value: String
) {
    override fun toString() = name
}