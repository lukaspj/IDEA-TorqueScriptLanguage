package org.lukasj.idea.torquescript.engine.model

class EngineClass(
    val name: String,
    val docs: String,
    val superType: String?,
    val isAbstract: Boolean,
    val isInstantiable: Boolean,
    val isDisposable: Boolean,
    val isSingleton: Boolean,
    val properties: List<EngineClassProperty>,
    val methods: List<EngineFunction>,
    val scopeList: List<String>
) {
    override fun toString() = name
}
