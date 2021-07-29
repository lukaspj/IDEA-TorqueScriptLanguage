package org.lukasj.idea.torquescript.engine.model

class EngineFunction(
    val name: String,
    val docs: String,
    val returnType: String,
    val symbol: String,
    val isCallback: Boolean,
    private val _isVariadic: Boolean,
    val arguments: List<EngineFunctionArgument>,
    val scopeList: List<String>
) {
    val isOverride: Boolean
    val isStatic: Boolean
    val isVariadic: Boolean

    init {
        isStatic =
            if (arguments.isNotEmpty() && arguments.first().name == "this") {
                arguments.first().typeName.split(':').last() != scopeList.last()
            } else {
                true
            }

        isVariadic = when {
            arguments.size == 2
                    && arguments.first().name == "argc"
                    && arguments.last().name == "argv" -> {
                true
            }
            arguments.size == 1
                    && arguments.first().name == "args"
                    && arguments.first().typeName == "StringVector" -> {
                true
            }
            else -> {
                _isVariadic
            }
        }

        isOverride = false
    }

    override fun toString() =
        if (scopeList.isNotEmpty()) {
            "${scopeList.last()}::$name"
        } else {
            name
        }
}

