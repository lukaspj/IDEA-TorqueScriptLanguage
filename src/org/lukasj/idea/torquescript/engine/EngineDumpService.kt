package org.lukasj.idea.torquescript.engine

import com.intellij.openapi.components.Service

data class ObjectField(
    val type: String,
    val name: String,
    val value: String
)

data class Param(
    val type: String,
    val name: String,
    val defaultValue: String?
)

data class ObjectMethod(
    val returnType: String,
    val name: String,
    val params: List<Param>
)

data class ObjectDump(
    val className: String = "",
    val staticFields: List<ObjectField> = listOf(),
    val dynamicFields: List<ObjectField> = listOf(),
    val methods: List<ObjectMethod> = listOf(),
    val callbacks: List<ObjectMethod> = listOf()
)

@Service
class EngineDumpService {
    fun readObjectDump(dump: String): ObjectDump {
        val dumpLines = dump.trim().lines()
        assert(dumpLines.any { it.matches(Regex("^Class:.*")) })
        return dumpLines.fold(Pair(ObjectDump(), "")) { acc, line ->
            when (line) {
                "Static Fields:",
                "Dynamic Fields:",
                "Methods:",
                "Callbacks:" ->
                    acc.copy(second = line)
                else ->
                    line.trim().split(' ')
                        .map { it.trim() }
                        .filter { it != "" }
                        .let { words ->
                            acc.copy(
                                first = when (acc.second) {
                                    "Static Fields:" ->
                                        acc.first.copy(
                                            staticFields = acc.first.staticFields
                                                .plus(
                                                    ObjectField(
                                                        words[0], words[1], words[3].trim('"')
                                                    )
                                                )
                                        )
                                    "Dynamic Fields:" ->
                                        acc.first.copy(
                                            dynamicFields = acc.first.dynamicFields
                                                .plus(
                                                    ObjectField(
                                                        words[0], words[1], words[3].trim('"')
                                                    )
                                                )
                                        )
                                    "Methods:" ->
                                        Regex("([a-zA-Z0-9_]+)\\s+([a-zA-Z0-9_]+)\\s*\\(([^)]*)\\)\\s*").find(line)!!
                                            .let { match ->
                                                acc.first.copy(
                                                    methods = acc.first.methods.plus(
                                                        ObjectMethod(
                                                            returnType = match.groupValues[1],
                                                            name = match.groupValues[2],
                                                            params = match.groupValues[3]
                                                                .split(',')
                                                                .map { it.trim() }
                                                                .filter { it != "" }
                                                                .map { it.split(' ', '=') }
                                                                .map { s -> s.map { it.trim() }.filter { it != "" } }
                                                                .map {
                                                                    Param(it[0], it[1], it.getOrNull(2))
                                                                }
                                                        )
                                                    )
                                                )
                                            }
                                    "Callbacks:" ->
                                        Regex("([a-zA-Z0-9_]+)\\s*\\(([^)]*)\\)\\s*").find(line)!!
                                            .let { match ->
                                                acc.first.copy(
                                                    callbacks = acc.first.callbacks.plus(
                                                        ObjectMethod(
                                                            returnType = "void",
                                                            name = match.groupValues[1],
                                                            params = match.groupValues[2]
                                                                .split(',')
                                                                .asSequence()
                                                                .map { it.trim() }
                                                                .filter { it != "" }
                                                                .map { it.split(' ', '=') }
                                                                .map { s -> s.map { it.trim() }.filter { it != "" } }
                                                                .map {
                                                                    Param(it[0], it[1], it.getOrNull(2))
                                                                }
                                                                .toList()
                                                        )
                                                    )
                                                )
                                            }
                                    else -> acc.first
                                }
                            )
                        }
            }
        }.first
    }
}