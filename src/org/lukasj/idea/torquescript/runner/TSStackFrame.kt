package org.lukasj.idea.torquescript.runner

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.startOffset
import com.intellij.ui.ColoredTextContainer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.PlatformIcons
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.intellij.xdebugger.frame.*
import com.intellij.xdebugger.impl.frame.XStackFrameContainerEx
import kotlinx.coroutines.runBlocking
import org.lukasj.idea.torquescript.psi.TSFile
import org.lukasj.idea.torquescript.reference.ReferenceUtil
import org.lukasj.idea.torquescript.telnet.TSTelnetClient
import javax.swing.Icon

class TSNamedValue(
    name: String,
    val value: String,
    val telnetClient: TSTelnetClient,
    val level: Int,
    val type: String?
) : XNamedValue(name) {

    var icon: Icon = PlatformIcons.VARIABLE_ICON

    companion object {
        suspend fun isObject(telnetClient: TSTelnetClient, level: Int, value: String) =
            telnetClient.evalAtLevel(level, "isObject($value)").let {
                it.toBoolean() || it == "1"
            }

        suspend fun getRepresentation(telnetClient: TSTelnetClient, level: Int, value: String) =
            if (isObject(telnetClient, level, value)) {
                if (value.toIntOrNull() == null) {
                    value
                } else {
                    telnetClient.evalAtLevel(level, "$value.name").let {
                        "$value ($it)"
                    }
                }
            } else {
                value
            }

        suspend fun getType(telnetClient: TSTelnetClient, level: Int, value: String) =
            if (isObject(telnetClient, level, value)) {
                telnetClient.evalAtLevel(level, "$value.getClassName()")
            } else {
                null
            }
    }

    override fun computePresentation(node: XValueNode, place: XValuePlace) =
        runBlocking {
            node.setPresentation(
                icon,
                getType(telnetClient, level, value) ?: type,
                getRepresentation(telnetClient, level, value),
                isObject(telnetClient, level, value)
            )
        }

    override fun computeChildren(node: XCompositeNode) {
        runBlocking {
            if (isObject(telnetClient, level, value)) {
                val valueList = XValueChildrenList()
                telnetClient.evalAtLevel(level, "$value.getFieldCount()").toIntOrNull()
                    ?.let { staticFieldCount ->
                        (0..staticFieldCount)
                            .map {
                                telnetClient.evalAtLevel(level, "$value.getField($it)")
                            }
                            .filter { it != "\"\"" }
                            .forEach { fieldName ->
                                valueList.add(
                                    TSNamedValue(
                                        fieldName,
                                        telnetClient.evalAtLevel(level, "$value.$fieldName"),
                                        telnetClient,
                                        level,
                                        telnetClient.evalAtLevel(level, "$value.getFieldType(\"$fieldName\")")
                                    )
                                )
                            }
                    }
                telnetClient.evalAtLevel(level, "$value.getDynamicFieldCount()").toIntOrNull()
                    ?.let { staticFieldCount ->
                        (0..staticFieldCount)
                            .map {
                                telnetClient.evalAtLevel(level, "$value.getDynamicField($it)")
                            }
                            .filter { it != "\"\"" }
                            .forEach { fieldName ->
                                valueList.add(
                                    TSNamedValue(
                                        fieldName,
                                        telnetClient.evalAtLevel(level, "$value.$fieldName"),
                                        telnetClient,
                                        level,
                                        "dynamic"
                                    )
                                )
                            }
                    }
                node.addChildren(valueList, true)
            }
        }
    }
}

class TSStackFrame(
    private val project: Project,
    private val position: XSourcePosition?,
    private val function: String,
    private val level: Int,
    private val telnetClient: TSTelnetClient
) : XStackFrame() {
    var paramValuesCache: String? = null

    val variables: XValueChildrenList = position?.let { position ->
        XValueChildrenList().let { valueList ->
            ApplicationManager.getApplication().runReadAction {
                ReferenceUtil.findLocalVariablesForContext(
                    (PsiUtilCore.getPsiFile(project, position.file) as TSFile).findElementAt(position.offset)!!
                ).filter {
                    (PsiDocumentManager.getInstance(project).getDocument(it.containingFile)
                        ?.getLineNumber(it.startOffset) ?: 0) <= position.line
                }.distinctBy { it.text }.map {
                    runBlocking {
                        TSNamedValue(
                            it.text,
                            telnetClient.evalAtLevel(level, it.text),
                            telnetClient,
                            level,
                            ReferenceUtil.tryResolveType(it)
                        )
                    }
                }.fold(valueList) { acc, namedValue ->
                    acc.also {
                        it.add(namedValue)
                    }
                }
            }
            valueList
        }
    } ?: XValueChildrenList()

    override fun getSourcePosition(): XSourcePosition? = position

    override fun getEvaluator(): XDebuggerEvaluator = TSDebuggerEvaluator(telnetClient, level)

    fun getParamValues(): String {
        if (paramValuesCache != null) {
            return paramValuesCache!!
        }

        if (position == null) return "<Unknown>"

        val tsFile = PsiUtilCore.getPsiFile(project, position.file) as TSFile
        val tsFunction = tsFile.getEnclosingFunction(
            PsiUtilCore.getElementAtOffset(
                tsFile, position.offset
            )
        )

        runBlocking {
            paramValuesCache =
                tsFunction?.getParameters()?.map { telnetClient.evalAtLevel(level, it.text) }?.joinToString { it } ?: ""
        }

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
    override fun getTopFrame(): XStackFrame? = stackFrameList.firstOrNull()

    override fun computeStackFrames(firstFrameIndex: Int, container: XStackFrameContainer?) {
        val stackFrameContainerEx = container as XStackFrameContainerEx
        stackFrameContainerEx.addStackFrames(stackFrameList, topFrame, true)
    }
}