package org.lukasj.idea.torquescript.documentation

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.project.Project
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.psi.impl.TSFunctionCallExpressionElementImpl


fun renderFunctionCallQuickInfo(element: TSFunctionCallExpressionElementImpl) =
    element.reference?.resolve().let { ref ->
        when {
            ref != null -> {
                renderLocationQuickInfo(ref.text, ref.containingFile.name)
            }
            element.name != null -> {
                element.project
                    .getService(EngineApiService::class.java)
                    .findFunction(element.name!!)
                    ?.let { function ->
                        renderBuiltinQuickInfo(
                            function.name + function.arguments.joinToString(
                                ", ",
                                "(",
                                ")"
                            ) { it.toArgString() }, "function"
                        )
                    }
            }
            else -> {
                null
            }
        }
    }


fun renderClassQuickInfo(project: Project, name: String) =
    project
        .getService(EngineApiService::class.java)
        .findClass(name)
        ?.let {
            renderBuiltinQuickInfo(it.name, "class")
        }

fun renderLocationQuickInfo(name: String, location: String) =
    StringBuilder()
        .append(DocumentationMarkup.DEFINITION_START)
        .append("$name in $location")
        .append(DocumentationMarkup.DEFINITION_END)
        .toString()

fun renderBuiltinQuickInfo(name: String, type: String) =
    StringBuilder()
        .append(DocumentationMarkup.DEFINITION_START)
        .append("$name is a builtin $type")
        .append(DocumentationMarkup.DEFINITION_END)
        .toString()
