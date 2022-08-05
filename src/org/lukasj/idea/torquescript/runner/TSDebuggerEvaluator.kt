package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.editor.Document
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.elementType
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import kotlinx.coroutines.runBlocking
import org.lukasj.idea.torquescript.psi.TSIdentExpression
import org.lukasj.idea.torquescript.psi.TSLiteralExpression
import org.lukasj.idea.torquescript.psi.TSTypes
import org.lukasj.idea.torquescript.psi.TSVarExpression
import org.lukasj.idea.torquescript.telnet.TSTelnetClient

class TSDebuggerEvaluator(private val telnetClient: TSTelnetClient, private val level: Int) : XDebuggerEvaluator() {
    override fun getExpressionRangeAtOffset(
        project: Project,
        document: Document,
        offset: Int,
        sideEffectsAllowed: Boolean
    ): TextRange? {
        var currentRange: TextRange? = null
        val tsFile = PsiDocumentManager.getInstance(project).getPsiFile(document) ?: return null

        val element = PsiUtilCore.getElementAtOffset(tsFile, offset)

        currentRange = when (element.elementType) {
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

        return currentRange
    }

    override fun evaluate(expression: String, callback: XEvaluationCallback, expressionPosition: XSourcePosition?) {
        ProgressManager.getInstance()
        (object : Task.Backgroundable(null, "Evaluating $expression") {
            override fun run(indicator: ProgressIndicator) =
                runBlocking {
                    telnetClient.evalAtLevel(level, expression)
                }.let {
                    callback.evaluated(
                        TSNamedValue(
                            expression,
                            it,
                            telnetClient,
                            level,
                            "unknown"
                        )
                    )
                }
        }).queue()
    }
}