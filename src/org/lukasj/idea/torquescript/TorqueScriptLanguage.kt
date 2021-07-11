package org.lukasj.idea.torquescript

import com.intellij.lang.Language

class TorqueScriptLanguage : Language("TorqueScript") {
    companion object {
        @JvmField
        val INSTANCE = TorqueScriptLanguage()
    }
}