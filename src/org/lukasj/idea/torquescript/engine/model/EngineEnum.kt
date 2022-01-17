package org.lukasj.idea.torquescript.engine.model

class EngineEnum(
    val name: String,
    val docs: String,
    val values: Collection<EngineEnumValue>,
    val scopeList: List<String>
) {
    override fun toString() = name
}

