package org.lukasj.idea.torquescript.engine.model

class EngineScope(
    val name: String,
    val docs: String,
    val functions: List<EngineFunction>,
    val enums: List<EngineEnum>,
    val classes: List<EngineClass>,
    val structs: List<EngineStruct>,
    val scopes: List<EngineScope>,
    val scopeList: Collection<String>
) {
    fun getAllFunctions(): List<EngineFunction> =
        functions
            .plus(
                scopes.flatMap { it.getAllFunctions() }
            )
            .plus(
                classes.flatMap { it.methods }
            )

    fun getAllClasses(): List<EngineClass> =
        classes
            .plus(
                scopes.flatMap { it.getAllClasses() }
            )
}