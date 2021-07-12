package org.lukasj.idea.torquescript

import com.intellij.lang.Language

class TSLanguage : Language("TorqueScript") {
    companion object {
        @JvmField
        val INSTANCE = TSLanguage()
    }
}