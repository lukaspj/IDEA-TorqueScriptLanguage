package org.lukasj.idea.torquescript.engine.model

class EngineClassProperty(
    val name: String,
    val docs: String,
    val typeName: String,
    val indexedSize: Int,
    val isConstant: Boolean,
    val isTransient: Boolean,
    val isVisible: Boolean
) {
    override fun toString() = name
}
