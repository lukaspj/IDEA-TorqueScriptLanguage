package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.util.xmlb.annotations.Property

class TSRunConfigurationOptions : RunConfigurationOptions() {
    @get:Property(surroundWithTag = true)
    var appPath by string()
    @get:Property(surroundWithTag = true)
    var workingDirectory by string()
    @get:Property(surroundWithTag = true)
    var mainScript by string()
}