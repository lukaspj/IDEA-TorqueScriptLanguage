package org.lukasj.idea.torquescript.symbols

import com.intellij.openapi.project.Project

abstract class TSCachedListGenerator<T> {
    abstract fun generate(project: Project): Collection<T>
}