package org.lukasj.idea.torquescript.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.editor.richcopy.SyntaxInfoBuilder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.engine.docstring.elements.*
import org.lukasj.idea.torquescript.engine.docstring.parsers.EngineApiDocStringParser
import org.lukasj.idea.torquescript.engine.model.EngineClass
import org.lukasj.idea.torquescript.engine.model.EngineFunction
import org.lukasj.idea.torquescript.psi.TSElementFactory
import org.lukasj.idea.torquescript.psi.TSFunctionIdentifier
import org.lukasj.idea.torquescript.psi.TSNewInstanceExpression
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.impl.TSDatablockDeclarationElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionCallExpressionElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSFunctionIdentifierElementImpl
import org.lukasj.idea.torquescript.psi.impl.TSIdentExpressionImpl

class TSDocumentationProvider : AbstractDocumentationProvider() {
    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        if (contextElement?.language != TSLanguage.INSTANCE) {
            return super.getCustomDocumentationElement(editor, file, contextElement, targetOffset)
        }

        return when (contextElement.parent) {
            is TSIdentExpressionImpl ->
                if (contextElement.parent.parent is TSFunctionCallExpressionElementImpl) {
                    return contextElement.parent.parent
                } else {
                    contextElement.parent
                }
            else -> contextElement
        }
    }


    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? =
        when (element) {
            is TSFunctionIdentifierElementImpl ->
                renderFunction(element)
            is TSFunctionCallExpressionElementImpl ->
                renderFunctionCall(element)
            is TSDatablockDeclarationElementImpl ->
                renderClassDoc(element.project, element.getClassName()!!)
            else -> {
                when (element?.parent) {
                    is TSNewInstanceExpression -> renderClassDoc(element.project, element.text)
                    is TSFunctionIdentifierElementImpl ->
                        if (element.nextSibling.elementType == TSTypes.COLON_COLON) {
                            renderClassDoc(element.project, element.text)
                        } else {
                            renderFunction(element.parent as TSFunctionIdentifierElementImpl)
                        }
                    else -> super.generateDoc(element, originalElement)
                }
            }
        }

    private fun renderClassDoc(project: Project, className: String): String {
        val engineApiService = project.getService(EngineApiService::class.java)
        val engineClass = engineApiService.findClass(className)
        return if (engineClass != null) {
            renderBuiltinClass(project, engineClass)
        } else {
            StringBuilder()
                .append(DocumentationMarkup.CONTENT_START)
                .append("Could not resolve $className to a class")
                .append(DocumentationMarkup.CONTENT_END)
                .toString()
        }
    }

    private fun renderFunctionCall(element: TSFunctionCallExpressionElementImpl) =
        element.reference?.resolve()
            .let {
                if (it == null) {
                    if (element.name != null) {
                        renderFunction(element.project, element.name!!)
                    } else {
                        null
                    }
                } else {
                    when (it) {
                        is TSFunctionIdentifierElementImpl -> renderFunction(it)
                        else -> null
                    }
                }
            }

    private fun renderFunction(function: TSFunctionIdentifierElementImpl): String {
        return StringBuilder()
            .append(DocumentationMarkup.DEFINITION_START)
            .append(function.name)
            .append(DocumentationMarkup.DEFINITION_END)
            .toString()
    }

    private fun renderFunction(project: Project, name: String): String {
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

    private fun renderBuiltinFunction(project: Project, function: EngineFunction): String =
        EngineApiDocStringParser()
            .parse(function.docs)
            .let { docString ->
                StringBuilder()
                    .append(DocumentationMarkup.CONTENT_START)
                    .append(function.name)
                    .append(function.arguments.joinToString(", ", "(", ")") { it.toArgString() })
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

    private fun renderBuiltinClass(project: Project, engineClass: EngineClass) =
        EngineApiDocStringParser()
            .parse(engineClass.docs)
            .let { docString ->
                StringBuilder()
                    .append(DocumentationMarkup.CONTENT_START)
                    .append(engineClass.name)
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

    private fun renderDocstring(project: Project, element: IDocElement): String =
        when (element) {
            is CompoundDocElement -> element.children.joinToString("") { renderDocstring(project, it) }
            is CodeExampleDocElement -> renderCodeExample(project, element)
            is TextDocElement -> element.text
            is DescriptionDocElement -> renderDescription(project, element)
            is SummaryDocElement -> ""
            is ParameterDocElement -> renderParameter(project, element)
            is ParameterRefDocElement -> StringBuilder().append(element.parameterName).toString()
            is ReturnDocElement -> renderReturn(project, element)
            is InternalDocElement -> ""
            is RemarkDocElement -> StringBuilder().append("<b>")
                .append(element.children.joinToString { renderDocstring(project, it) }).append("</b>").toString()
            is InGroupDocElement -> renderInGroup(project, element)
            is NullDocElement -> ""
            else -> "(${element.javaClass.name} not handled in docstring)"
        }

    private fun renderSummary(project: Project, element: SummaryDocElement) =
        StringBuilder()
            .append(DocumentationMarkup.CONTENT_START)
            .append(
                element.children.joinToString("") { renderDocstring(project, it) }
            )
            .append(DocumentationMarkup.CONTENT_END)
            .toString()


    private fun renderInGroup(project: Project, element: InGroupDocElement) =
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

    private fun renderReturn(project: Project, element: ReturnDocElement) =
        StringBuilder()
            .append(DocumentationMarkup.SECTION_HEADER_START)
            .append("returns")
            .append(DocumentationMarkup.SECTION_SEPARATOR)
            .append(
                element.children.joinToString("") { renderDocstring(project, it) }
            )
            .append(DocumentationMarkup.SECTION_END)
            .toString()

    private fun renderParameter(project: Project, element: ParameterDocElement) =
        StringBuilder()
            .append(DocumentationMarkup.SECTION_HEADER_START)
            .append(element.parameterName)
            .append(DocumentationMarkup.SECTION_SEPARATOR)
            .append(
                element.children.joinToString("") { renderDocstring(project, it) }
            )
            .append(DocumentationMarkup.SECTION_END)
            .toString()

    private fun renderDescription(project: Project, element: DescriptionDocElement) =
        StringBuilder()
            .append(DocumentationMarkup.SECTION_HEADER_START)
            .append("description")
            .append(DocumentationMarkup.SECTION_SEPARATOR)
            .append(
                element.children.joinToString("") { renderDocstring(project, it) }
            )
            .append(DocumentationMarkup.SECTION_END)
            .toString()

    private fun renderCodeExample(project: Project, element: CodeExampleDocElement) =
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
}