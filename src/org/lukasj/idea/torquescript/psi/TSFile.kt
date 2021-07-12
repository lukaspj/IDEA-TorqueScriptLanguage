package org.lukasj.idea.torquescript.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.lukasj.idea.torquescript.TSFileType
import org.lukasj.idea.torquescript.TSLanguage

class TSFile(viewProvider:  FileViewProvider) : PsiFileBase(viewProvider, TSLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return TSFileType.INSTANCE
    }

    override fun toString(): String {
        return "TorqueScript File"
    }
}