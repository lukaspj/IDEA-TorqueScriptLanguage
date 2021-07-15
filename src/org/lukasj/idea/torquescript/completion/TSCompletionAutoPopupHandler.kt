package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.patterns.PsiJavaPatterns
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiKeyword
import org.lukasj.idea.torquescript.psi.TSFile

class TSCompletionAutoPopupHandler : TypedHandlerDelegate() {
    override fun checkAutoPopup(charTyped: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (file !is TSFile) {
            return Result.CONTINUE
        }
        if (LookupManager.getActiveLookup(editor) != null) {
            return Result.CONTINUE
        }

        val offset = editor.caretModel.offset
        if (charTyped == ' ' &&
            StringUtil.endsWith(editor.document.immutableCharSequence, 0, offset, PsiKeyword.NEW)
        ) {
            AutoPopupController.getInstance(project).scheduleAutoPopup(
                editor, CompletionType.BASIC
            ) { f: PsiFile ->
                val leaf = f.findElementAt(offset - PsiKeyword.NEW.length)
                leaf is PsiKeyword &&
                        leaf.textMatches(PsiKeyword.NEW) &&
                        !PsiJavaPatterns.psiElement().insideStarting(PsiJavaPatterns.psiExpressionStatement())
                            .accepts(leaf)
            }
            return Result.STOP
        }

        //Second colon on a :: should pop it
        if (charTyped == ':' &&
            StringUtil.endsWith(editor.document.immutableCharSequence, 0, offset, ":")) {
            AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
            return Result.STOP
        }

        //Variable prefixes
        if (charTyped == '%' || charTyped == '$') {
            AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
            return Result.STOP
        }

        return super.checkAutoPopup(charTyped, project, editor, file)
    }
}