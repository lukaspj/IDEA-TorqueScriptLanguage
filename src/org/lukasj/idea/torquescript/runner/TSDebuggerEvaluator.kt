package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.elementType
import com.intellij.util.PlatformIcons
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XValue
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.frame.XValuePlace
import org.lukasj.idea.torquescript.psi.*

class TSDebuggerEvaluator(private val telnetClient: TSTelnetClient, private val level: Int) : XDebuggerEvaluator() {
    override fun getExpressionRangeAtOffset(
        project: Project,
        document: Document,
        offset: Int,
        sideEffectsAllowed: Boolean
    ): TextRange? {
        var currentRange: TextRange? = null
        PsiDocumentManager.getInstance(project).commitAndRunReadAction {
            val tsFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return@commitAndRunReadAction

            val element = PsiUtil.getElementAtOffset(tsFile, offset)

            currentRange = when(element.elementType) {
                TSTypes.LOCALVAR -> element.textRange
                TSTypes.GLOBALVAR -> element.textRange
                TSTypes.FLOAT -> element.textRange
                TSTypes.INTEGER -> element.textRange
                TSTypes.HEXDIGIT -> element.textRange
                TSTypes.QUOTED_STRING -> element.textRange
                TSTypes.TAGGED_STRING -> element.textRange
                else -> currentRange
            }

            currentRange = when (element) {
                is TSVarExpression -> element.textRange
                is TSLiteralExpression -> element.textRange
                is TSIdentExpression -> element.textRange
                else -> currentRange
            }

            currentRange = PsiTreeUtil.findFirstParent(element) {
                it is TSIdentExpression
            }?.textRange ?: currentRange
        }

        return currentRange
    }

    override fun evaluate(expression: String, callback: XEvaluationCallback, expressionPosition: XSourcePosition?) {
        val result = telnetClient.evalAtLevel(level, expression)
        callback.evaluated(object : XValue() {
            override fun computePresentation(node: XValueNode, place: XValuePlace) {
                node.setPresentation(PlatformIcons.VARIABLE_ICON, "string", result, false)
            }
        })
    }
}