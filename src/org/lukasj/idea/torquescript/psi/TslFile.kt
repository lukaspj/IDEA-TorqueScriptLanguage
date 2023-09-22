package org.lukasj.idea.torquescript.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.lukasj.idea.torquescript.TslFileType
import org.lukasj.idea.torquescript.TslLanguage

open class TslFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, TslLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return TslFileType.INSTANCE
    }

    override fun toString(): String {
        return "Torque Shader Language File"
    }
}