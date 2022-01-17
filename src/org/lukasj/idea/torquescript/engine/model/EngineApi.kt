package org.lukasj.idea.torquescript.engine.model

class EngineApi(rootScope: EngineScope) {
    private val scope = rootScope

    fun getAllFunctions(): List<EngineFunction> = scope.getAllFunctions()
    fun getAllClasses(): List<EngineClass> = scope.getAllClasses()
    fun getAllEnums(): List<EngineEnum> = scope.getAllEnums()
}

