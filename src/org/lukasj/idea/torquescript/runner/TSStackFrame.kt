package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiUtilCore
import com.intellij.refactoring.suggested.startOffset
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.PlatformIcons
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.impl.frame.XStackFrameContainerEx
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.reference.ReferenceUtil

class TSValue(name: String, val value: String, val type: String?) : XNamedValue(name) {
    override fun computePresentation(node: XValueNode, place: XValuePlace) {
        node.setPresentation(
            PlatformIcons.VARIABLE_ICON,
            type,
            value,
            false
        )
    }
}

class TSStackFrame(
    private val project: Project,
    private val position: XSourcePosition,
    private val function: String,
    private val level: Int,
    private val telnetClient: TSTelnetClient
) : XStackFrame() {
    var paramValuesCache: String? = null

    val variables: XValueChildrenList =
        XValueChildrenList()
            .let { valueList ->
                ApplicationManager.getApplication().runReadAction {
                    ReferenceUtil.findLocalVariablesForContext(
                        (PsiUtilCore.getPsiFile(project, position.file) as TSFile)
                            .findElementAt(position.offset)!!
                    )
                        .filter {
                            PsiDocumentManager.getInstance(project)
                                .getDocument(it.containingFile)
                                ?.getLineNumber(it.startOffset) ?: 0 <= position.line
                        }
                        .distinctBy { it.text }
                        .map { TSValue(it.text, telnetClient.evalAtLevel(level, it.text), ReferenceUtil.tryResolveType(it)) }
                        .fold(valueList) { acc, namedValue ->
                            acc.also {
                                it.add(namedValue)
                            }
                        }
                }
                valueList
            }

    override fun getSourcePosition(): XSourcePosition = position

    override fun getEvaluator(): XDebuggerEvaluator =
        TSDebuggerEvaluator(telnetClient, level)

    fun getParamValues(): String {
        if (paramValuesCache != null) {
            return paramValuesCache!!
        }

        val tsFile = PsiUtilCore.getPsiFile(project, position.file) as TSFile
        val tsFunction = tsFile.getEnclosingFunction(
            PsiUtilCore.getElementAtOffset(
                tsFile, position.offset
            )
        )

        paramValuesCache =
            tsFunction?.getParameters()
                ?.map { telnetClient.evalAtLevel(level, it.text) }
                ?.joinToString { it }
                ?: ""

        return paramValuesCache!!
    }


    override fun computeChildren(node: XCompositeNode) {
        node.addChildren(variables, true)
    }

    override fun customizePresentation(component: ColoredTextContainer) {
        component.setIcon(PlatformIcons.FUNCTION_ICON)

        component.append("$function(${getParamValues()}) (", SimpleTextAttributes.REGULAR_ATTRIBUTES)
        super.customizePresentation(component)
        component.append(")", SimpleTextAttributes.REGULAR_ATTRIBUTES)
    }
}

class TSExecutionStack(private val stackFrameList: List<XStackFrame>) : XExecutionStack("TorqueScriptStack") {
    override fun getTopFrame(): XStackFrame? =
        stackFrameList.firstOrNull()

    override fun computeStackFrames(firstFrameIndex: Int, container: XStackFrameContainer?) {
        val stackFrameContainerEx = container as XStackFrameContainerEx
        stackFrameContainerEx.addStackFrames(stackFrameList, topFrame, true)
    }
}