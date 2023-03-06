package org.lukasj.idea.torquescript.completion

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.editor.EditorModificationUtil

class TSMethodCallInsertHandler : InsertHandler<LookupElement> {
    override fun handleInsert(context: InsertionContext, item: LookupElement) {
        val elementBeingEdited = context.file.findElementAt(context.startOffset)
        if (elementBeingEdited != null) {
            // Remove the current element
            context.editor.document.deleteString(elementBeingEdited.textOffset, context.editor.caretModel.currentCaret.offset)
            // Insert the new element with proper casing
            EditorModificationUtil.insertStringAtCaret(context.editor, "${item.lookupString}()", true, item.lookupString.length + 1)
        }
    }

    companion object {
        val INSTANCE = TSMethodCallInsertHandler()
    }
}