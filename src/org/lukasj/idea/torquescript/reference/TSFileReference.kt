package org.lukasj.idea.torquescript.reference

import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import org.lukasj.idea.torquescript.TSFileUtil
import org.lukasj.idea.torquescript.psi.TSLiteralExpression
import org.lukasj.idea.torquescript.psi.impl.TSAssignmentExpressionImpl
import org.lukasj.idea.torquescript.psi.impl.TSLiteralExpressionElementImpl
import org.lukasj.idea.torquescript.taml.TamlModuleService
import java.io.File

class TSFileReference(literal: PsiElement, rangeInElement: TextRange, val isAssetPath: Boolean = false) :
    PsiReferenceBase<PsiElement>(literal, rangeInElement) {
    override fun resolve(): PsiElement? {
        val value =
            concatenateSiblings(element.text.substring(rangeInElement.startOffset, rangeInElement.endOffset), element)
        return TSFileUtil.resolveScriptPath(element, value, isAssetPath)
            ?.let { VfsUtil.findFile(it, true) }
            ?.let {
                PsiManager.getInstance(element.project).findFile(it)
            }
    }

    private fun concatenateSiblings(path: String, element: PsiElement): String {
        return concatenateBackward(concatenateForward(path, element), element)
    }

    private fun concatenateBackward(path: String, element: PsiElement): String {
        val prevValidSibling = PsiTreeUtil.skipWhitespacesBackward(
            PsiTreeUtil.skipWhitespacesBackward(
                element
            )
        )
        if (prevValidSibling != null && prevValidSibling.reference is TSGlobalVarReference) {
            val references = (prevValidSibling.reference as TSGlobalVarReference).multiResolve(false)
            if (references.size == 1 && references[0].element?.parent is TSAssignmentExpressionImpl) {
                val value = references[0].element?.parent?.lastChild
                if (value != null) {
                    when (value) {
                        is TSLiteralExpression ->
                            return "${value.text.substring(1, value.textLength - 1)}$path"
                    }
                }
            }
        }
        return path
    }

    private fun concatenateForward(path: String, element: PsiElement?): String {
        val nextValidSibling = PsiTreeUtil.skipWhitespacesForward(
            PsiTreeUtil.skipWhitespacesForward(
                element
            )
        )
        if (nextValidSibling != null && nextValidSibling.reference is TSGlobalVarReference) {
            if (nextValidSibling.text == "\$TorqueScriptFileExtension") {
                return "${path}tscript"
            }

            val references = (nextValidSibling.reference as TSGlobalVarReference).multiResolve(false)
            println(references)
        }
        return path
    }
}