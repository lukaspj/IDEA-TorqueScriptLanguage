package org.lukasj.idea.torquescript.editor

import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import org.lukasj.idea.torquescript.reference.ReferenceUtil

class TSChooseByNameContributor : ChooseByNameContributor {
    override fun getNames(project: Project, includeNonProjectItems: Boolean): Array<String> =
        ReferenceUtil.getFunctions(project)
            .filter { it.name != null && it.name!!.isNotEmpty() }
            .map { function ->
                function.name!!
            }.toTypedArray()

    override fun getItemsByName(
        name: String,
        pattern: String?,
        project: Project,
        includeNonProjectItems: Boolean
    ): Array<NavigationItem> =
        ReferenceUtil.findFunction(project, name)
            .toTypedArray()
}