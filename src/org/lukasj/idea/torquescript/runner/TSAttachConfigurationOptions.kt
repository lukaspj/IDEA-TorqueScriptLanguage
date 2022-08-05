package org.lukasj.idea.torquescript.runner

import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.util.xmlb.annotations.Property

class TSAttachConfigurationOptions : RunConfigurationOptions() {
    @get:Property(surroundWithTag = true)
    var port by property(4200)
    @get:Property(surroundWithTag = true)
    var host by string()
    @get:Property(surroundWithTag = true)
    var password by string()
}