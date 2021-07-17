package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.util.xmlb.annotations.Property

class TSRunConfigurationOptions : RunConfigurationOptions() {
    @get:Property(surroundWithTag = true)
    var appPath by string()
}