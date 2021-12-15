package org.lukasj.idea.torquescript.engine.model

class EngineClassProperty(
    val name: String,
    val docs: String,
    val typeName: String,
    val indexedSize: String,
    val isConstant: String,
    val isTransient: String,
    val isVisible: String
) {
    override fun toString() = name
}
