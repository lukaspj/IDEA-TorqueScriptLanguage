package org.lukasj.idea.torquescript.documentation

import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.project.Project
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.engine.docstring.elements.*
import org.lukasj.idea.torquescript.engine.docstring.parsers.EngineApiDocStringParser
import org.lukasj.idea.torquescript.engine.model.EngineClass
import org.lukasj.idea.torquescript.engine.model.EngineFunction
import org.lukasj.idea.torquescript.psi.TSElementFactory
import org.lukasj.idea.torquescript.psi.impl.*
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.reference.TSFunctionReference
import org.lukasj.idea.torquescript.util.TSCachedObject
import org.lukasj.idea.torquescript.util.TSTypeLookupService


fun renderClassDoc(project: Project, className: String): String {
    val engineApiService = project.getService(EngineApiService::class.java)
    val typeLookupService = project.getService(TSTypeLookupService::class.java)
    val objectType = typeLookupService.findObject(project, className).singleOrNull()
    val engineClass = engineApiService.findClass(className)

    return when {
        engineClass != null -> {
            renderBuiltinClass(project, engineClass)
        }

        objectType != null -> {
            renderObjectWithClass(project, objectType)
        }

        else -> {
            StringBuilder()
                .append(DocumentationMarkup.CONTENT_START)
                .append("Could not resolve $className to a class")
                .append(DocumentationMarkup.CONTENT_END)
                .toString()
        }
    }
}

fun renderFunctionCall(element: TSFunctionCallExpressionElementImpl) =
    element.reference?.let { renderFunctionReference(it as TSFunctionReference) }

fun renderFunctionCall(element: TSPropertyElementImpl) =
    element.reference?.let { renderFunctionReference(it as TSFunctionReference) }

fun renderVariable(element: TSVarExpressionElementImpl) =
    ReferenceUtil.tryResolveType(element)?.let { resolvedType ->
        renderClassDoc(element.project, resolvedType)
    } ?: StringBuilder()
        .append(DocumentationMarkup.CONTENT_START)
        .append("Could not resolve the type of ${element.text}")
        .append(DocumentationMarkup.CONTENT_END)
        .toString()

fun renderFunctionReference(funcRef: TSFunctionReference) =
    funcRef.resolve()
        .let { element ->
            if (element == null) {
                val functionName = funcRef.functionName ?: funcRef.element.name
                if (functionName != null) {
                    if (funcRef.namespace == null) {
                        renderBuiltinFunction(funcRef.element.project, functionName)
                    } else {
                        funcRef.element.project.getService(TSTypeLookupService::class.java)
                            .getNamespaces(funcRef.namespace, funcRef.element.project)
                            .reversed().firstOrNull {
                                funcRef.element.project.getService(EngineApiService::class.java)
                                    .findMethod(it, functionName) != null
                            }
                            ?.let { renderBuiltinFunction(funcRef.element.project, "${it}::${functionName}") }
                    }
                } else {
                    null
                }
            } else {
                when (element) {
                    is TSFunctionStatementElementImpl -> renderFunctionStatement(element)
                    is TSFunctionIdentifierElementImpl -> renderFunctionIdentifier(element)
                    else -> null
                }
            }
        }

fun renderFunctionStatement(function: TSFunctionStatementElementImpl): String {
    return EngineApiDocStringParser()
        .parse(function.getDocstring() ?: "")
        .let { docString ->
            StringBuilder()
                .append(DocumentationMarkup.CONTENT_START)
                .append(function.name)
                .append(function.getParameters().joinToString(", ", "(", ")") { it.text })
                .append(
                    docString.children
                        .flatMap { it.children }
                        .filterIsInstance<SummaryDocElement>().map { renderSummary(function.project, it) }
                        .firstOrNull() ?: ""
                )
                .append(DocumentationMarkup.CONTENT_END)
                .append(DocumentationMarkup.SECTIONS_START)
                .append(
                    renderDocstring(
                        function.project,
                        docString
                    )

                )
                .append(DocumentationMarkup.SECTIONS_END)
                .toString()
        }
}

fun renderFunctionIdentifier(function: TSFunctionIdentifierElementImpl): String {
    return StringBuilder()
        .append(DocumentationMarkup.DEFINITION_START)
        .append(function.name)
        .append(DocumentationMarkup.DEFINITION_END)
        .toString()
}

fun renderBuiltinFunction(project: Project, name: String): String {
    val engineApiService = project.getService(EngineApiService::class.java)
    val function = engineApiService.findFunction(name)
    return if (function != null) {
        renderBuiltinFunction(project, function)
    } else {
        StringBuilder()
            .append(DocumentationMarkup.CONTENT_START)
            .append("Could not resolve $name to a function")
            .append(DocumentationMarkup.CONTENT_END)
            .toString()
    }
}

fun renderBuiltinFunction(project: Project, function: EngineFunction): String =
    EngineApiDocStringParser()
        .parse(function.docs)
        .let { docString ->
            StringBuilder()
                .append(DocumentationMarkup.DEFINITION_START)
                .append(function.name)
                .append(function.arguments.joinToString(", ", "(", ")") { it.toArgString() })
                .append(DocumentationMarkup.DEFINITION_END)
                .append(DocumentationMarkup.CONTENT_START)
                .append(
                    docString.children
                        .flatMap { it.children }
                        .filterIsInstance<SummaryDocElement>().map { renderSummary(project, it) }
                        .firstOrNull() ?: ""
                )
                .append(DocumentationMarkup.CONTENT_END)
                .append(DocumentationMarkup.SECTIONS_START)
                .append(
                    renderDocstring(
                        project,
                        docString
                    )

                )
                .append(DocumentationMarkup.SECTIONS_END)
                .toString()
        }

fun renderBuiltinClass(project: Project, engineClass: EngineClass) =
    StringBuilder()
        .append(DocumentationMarkup.DEFINITION_START)
        .append(engineClass.name)
        .append(DocumentationMarkup.DEFINITION_END)
        .append(renderBuiltinClassBody(project, engineClass))
        .toString()

fun renderBuiltinClassBody(project: Project, engineClass: EngineClass) =
    EngineApiDocStringParser()
        .parse(engineClass.docs)
        .let { docString ->
            StringBuilder()
                .append(DocumentationMarkup.CONTENT_START)
                .append(
                    docString.children
                        .flatMap { it.children }
                        .filterIsInstance<SummaryDocElement>().map { renderSummary(project, it) }
                        .firstOrNull() ?: ""
                )
                .append(DocumentationMarkup.CONTENT_END)
                .append(DocumentationMarkup.SECTIONS_START)
                .append(
                    renderDocstring(
                        project,
                        docString
                    )
                )
                .append(DocumentationMarkup.SECTIONS_END)
                .toString()
        }


fun renderObjectWithClass(project: Project, objectType: TSCachedObject) =
    project.getService(EngineApiService::class.java).let { engineApiService ->
        engineApiService.findClass(objectType.type)
            .let { engineClass ->
                if (engineClass != null) {
                    StringBuilder()
                        .append(DocumentationMarkup.DEFINITION_START)
                        .append(objectType.name)
                        .append(" : ")
                        .append(objectType.type)
                        .append(DocumentationMarkup.DEFINITION_END)
                        .append(renderBuiltinClassBody(project, engineClass))
                        .toString()
                } else {
                    StringBuilder()
                        .append(DocumentationMarkup.DEFINITION_START)
                        .append(objectType.name)
                        .append(" : ")
                        .append(objectType.type)
                        .append(DocumentationMarkup.DEFINITION_END)
                        .append(DocumentationMarkup.CONTENT_START)
                        .append("Could not resolve ${objectType.type} to a class")
                        .append(DocumentationMarkup.CONTENT_END)
                        .toString()
                }
            }
    }

fun renderDocstring(project: Project, element: IDocElement): String =
    when (element) {
        is CompoundDocElement -> element.children.joinToString("") { renderDocstring(project, it) }
        is CodeExampleDocElement -> renderCodeExample(project, element)
        is TextDocElement -> element.text
        is DescriptionDocElement -> renderDescription(project, element)
        is SummaryDocElement -> ""
        is ParameterDocElement -> renderParameter(project, element)
        is ParameterRefDocElement -> StringBuilder().append(element.parameterName).toString()
        is ReturnDocElement -> renderReturn(project, element)
        is SeeDocElement -> renderSee(project, element)
        is InternalDocElement -> ""
        is RemarkDocElement -> StringBuilder().append("<b>")
            .append(element.children.joinToString { renderDocstring(project, it) }).append("</b>").toString()

        is InGroupDocElement -> renderInGroup(project, element)
        is NullDocElement -> ""
        else -> "(${element.javaClass.name} not handled in docstring)"
    }

fun renderSummary(project: Project, element: SummaryDocElement) =
    StringBuilder()
        .append(DocumentationMarkup.CONTENT_START)
        .append(
            element.children.joinToString("") { renderDocstring(project, it) }
        )
        .append(DocumentationMarkup.CONTENT_END)
        .toString()


fun renderInGroup(project: Project, element: InGroupDocElement) =
    StringBuilder()
        .append(DocumentationMarkup.SECTION_HEADER_START)
        .append("in group")
        .append(DocumentationMarkup.SECTION_SEPARATOR)
        .append(element.groupName)
        .append(
            element.children.joinToString("") { renderDocstring(project, it) }
        )
        .append(DocumentationMarkup.SECTION_END)
        .toString()

fun renderReturn(project: Project, element: ReturnDocElement) =
    StringBuilder()
        .append(DocumentationMarkup.SECTION_HEADER_START)
        .append("returns")
        .append(DocumentationMarkup.SECTION_SEPARATOR)
        .append(
            element.children.joinToString("") { renderDocstring(project, it) }
        )
        .append(DocumentationMarkup.SECTION_END)
        .toString()

fun renderSee(project: Project, element: SeeDocElement) =
    StringBuilder()
        .append(DocumentationMarkup.SECTION_HEADER_START)
        .append("see")
        .append(DocumentationMarkup.SECTION_SEPARATOR)
        .append(
            element.seeReference
        )
        .append(DocumentationMarkup.SECTION_END)
        .toString()

fun renderParameter(project: Project, element: ParameterDocElement) =
    StringBuilder()
        .append(DocumentationMarkup.SECTION_HEADER_START)
        .append(element.parameterName)
        .append(DocumentationMarkup.SECTION_SEPARATOR)
        .append(
            element.children.joinToString("") { renderDocstring(project, it) }
        )
        .append(DocumentationMarkup.SECTION_END)
        .toString()

fun renderDescription(project: Project, element: DescriptionDocElement) =
    // Trim empty descriptions
    if (element.children.joinToString("") { renderDocstring(project, it) }.trim() == "") {
        ""
    } else {
        StringBuilder()
            .append(DocumentationMarkup.SECTION_HEADER_START)
            .append("description")
            .append(DocumentationMarkup.SECTION_SEPARATOR)
            .append(
                element.children.joinToString("") { renderDocstring(project, it) }
            )
            .append(DocumentationMarkup.SECTION_END)
            .toString()
    }

fun renderCodeExample(project: Project, element: CodeExampleDocElement) =
    StringBuilder()
        .append(DocumentationMarkup.SECTION_HEADER_START)
        .append("example")
        .append(DocumentationMarkup.SECTION_SEPARATOR)
        .append(
            element.children.joinToString("") {
                renderDocstring(project, it)
            }.let { text ->
                HtmlSyntaxInfoUtil.getHtmlContent(
                    TSElementFactory.createFile(project, text),
                    text,
                    null,
                    EditorColorsManager.getInstance().globalScheme,
                    0, text.length
                )?.toString() ?: text
            }
        )
        .append(DocumentationMarkup.SECTION_END)
        .toString()