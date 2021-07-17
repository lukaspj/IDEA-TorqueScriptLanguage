package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiUtil
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.PlatformIcons
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.XExecutionStack
import com.intellij.xdebugger.frame.XStackFrame
import com.intellij.xdebugger.impl.frame.XStackFrameContainerEx
import org.lukasj.idea.torquescript.psi.TSFile

class TSStackFrame(
    private val project: Project,
    private val position: XSourcePosition,
    private val function: String,
    private val level: Int,
    private val telnetClient: TSTelnetClient
) : XStackFrame() {
    var paramValuesCache: String? = null

    override fun getSourcePosition(): XSourcePosition = position

    override fun getEvaluator(): XDebuggerEvaluator =
        TSDebuggerEvaluator(telnetClient, level)

    fun getParamValues(): String {
        if (paramValuesCache != null) {
            return paramValuesCache!!
        }

        val tsFile = PsiUtil.getPsiFile(project, position.file) as TSFile
        val tsFunction = tsFile.getEnclosingFunction(
            PsiUtil.getElementAtOffset(
                tsFile, position.offset)
        )

        paramValuesCache =
            tsFunction?.getParameters()
                ?.map { telnetClient.evalAtLevel(level, it.text) }
                ?.joinToString { it }
                ?: ""

        return paramValuesCache!!
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