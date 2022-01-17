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
    val defaultValue: String
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
        val dumpLines = dump.lines()
        assert(dumpLines.any { it.matches(Regex("^Class:")) })
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
                        .let {
                            acc.copy(
                                first = when (acc.second) {
                                    "Static Fields:" ->
                                        acc.first.copy(
                                            staticFields = acc.first.staticFields
                                                .plus(
                                                    ObjectField(
                                                        it[0], it[1], it[3]
                                                    )
                                                )
                                        )
                                    "Dynamic Fields:" ->
                                        acc.first.copy(
                                            dynamicFields = acc.first.dynamicFields
                                                .plus(
                                                    ObjectField(
                                                        it[0], it[1], it[3]
                                                    )
                                                )
                                        )
                                    "Methods:" ->
                                        Regex("([^(]+)\\(\\s*([^,]*)*\\)").find(it[1])!!
                                            .let { match ->
                                                acc.first.copy(
                                                    methods = acc.first.methods.plus(
                                                        ObjectMethod(
                                                            returnType = it[0],
                                                            name = match.groupValues.drop(1).first(),
                                                            params = match.groupValues.drop(2)
                                                                .map { paramMatch ->
                                                                paramMatch
                                                                    .let { it.trim().split(' ', '=') }
                                                                    .let {
                                                                        Param(it[0], it[1], it[2])
                                                                    }
                                                                }
                                                        )
                                                    )
                                                )
                                            }
                                    "Callbacks:" ->
                                        Regex("([^(]+)\\(\\s*([^,]*)*\\)").find(it[1])!!
                                            .let { match ->
                                                acc.first.copy(
                                                    callbacks = acc.first.callbacks.plus(
                                                        ObjectMethod(
                                                            returnType = it[0],
                                                            name = match.groupValues.drop(1).first(),
                                                            params = match.groupValues.drop(2)
                                                                .map { paramMatch ->
                                                                    paramMatch
                                                                        .let { it.trim().split(' ', '=') }
                                                                        .let {
                                                                            Param(it[0], it[1], it[2])
                                                                        }
                                                                }
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