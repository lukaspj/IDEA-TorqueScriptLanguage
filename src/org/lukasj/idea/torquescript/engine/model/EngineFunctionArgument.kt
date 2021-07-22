package org.lukasj.idea.torquescript.engine.model

class EngineFunctionArgument(val name: String, val typeName: String, val defaultValue: String?) {
    fun toArgString() = "$typeName $name" + if (defaultValue != null) " = $defaultValue" else ""
}