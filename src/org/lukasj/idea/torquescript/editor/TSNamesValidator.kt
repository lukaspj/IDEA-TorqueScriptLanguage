package org.lukasj.idea.torquescript.editor

import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.project.Project
import org.lukasj.idea.torquescript.completion.TSKeywordCompletionContributor

class TSNamesValidator : NamesValidator {
    override fun isKeyword(name: String, project: Project?): Boolean =
        TSKeywordCompletionContributor.KEYWORDS.any { it == name }

    override fun isIdentifier(name: String, project: Project?): Boolean =
        name.matches(Regex("[%\$]?[A-Za-z_]([:A-Za-z0-9_]*[A-Za-z0-9_])*"))
                || name.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]"))
}