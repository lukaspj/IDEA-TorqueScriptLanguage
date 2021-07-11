package org.lukasj.idea.torquescript.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.lukasj.idea.torquescript.TorqueScriptFileType
import org.lukasj.idea.torquescript.TorqueScriptLanguage

class TorqueScriptFile(viewProvider:  FileViewProvider) : PsiFileBase(viewProvider, TorqueScriptLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return TorqueScriptFileType.INSTANCE
    }

    override fun toString(): String {
        return "TorqueScript File"
    }
}