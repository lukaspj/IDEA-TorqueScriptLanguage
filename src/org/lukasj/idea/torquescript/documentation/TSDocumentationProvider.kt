package org.lukasj.idea.torquescript.documentation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.elementType
import org.lukasj.idea.torquescript.TSLanguage
import org.lukasj.idea.torquescript.engine.EngineApiService
import org.lukasj.idea.torquescript.engine.docstring.elements.*
import org.lukasj.idea.torquescript.engine.docstring.parsers.EngineApiDocStringParser
import org.lukasj.idea.torquescript.engine.model.EngineClass
import org.lukasj.idea.torquescript.engine.model.EngineFunction
import org.lukasj.idea.torquescript.psi.*
import org.lukasj.idea.torquescript.psi.impl.*
import org.lukasj.idea.torquescript.reference.TSFunctionReference

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

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?) =
        when (element) {
            is TSFunctionIdentifierElementImpl ->
                renderFunctionIdentifier(element)
            is TSFunctionCallExpressionElementImpl ->
                renderFunctionCall(element)
            is TSPropertyImpl ->
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
                            renderFunctionIdentifier(element.parent as TSFunctionIdentifierElementImpl)
                        }
                    is TSProperty ->
                        element.parent.reference
                            .let {
                                when (it) {
                                    is TSFunctionReference ->
                                        renderFunctionReference(it)
                                    else -> super.generateDoc(element, originalElement)
                                }
                            }
                    else -> super.generateDoc(element, originalElement)
                }
            }
        }

    override fun getDocumentationElementForLookupItem(
        psiManager: PsiManager,
        obj: Any,
        element: PsiElement
    ): PsiElement? {
        return if (obj is PsiElement) {
            obj
        } else {
            println(element.text)
            null
        }
    }

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?) =
        when (element) {
            is TSFunctionIdentifierElementImpl ->
                renderLocationQuickInfo(element.text, element.containingFile.name)
            is TSFunctionCallExpressionElementImpl -> renderFunctionCallQuickInfo(element)
            is TSDatablockDeclarationElementImpl ->
                renderClassQuickInfo(element.project, element.text)
            else -> {
                when (element?.parent) {
                    is TSNewInstanceExpression -> renderClassQuickInfo(element.project, element.text)
                    is TSFunctionIdentifierElementImpl ->
                        if (element.nextSibling.elementType == TSTypes.COLON_COLON) {
                            renderClassQuickInfo(element.project, element.text)
                        } else {
                            renderLocationQuickInfo(element.parent.text, element.parent.containingFile.name)
                        }
                    is TSFunctionCallExpressionElementImpl ->
                        renderFunctionCallQuickInfo(element.parent as TSFunctionCallExpressionElementImpl)
                    else -> super.generateDoc(element, originalElement)
                }
            }
        }

}