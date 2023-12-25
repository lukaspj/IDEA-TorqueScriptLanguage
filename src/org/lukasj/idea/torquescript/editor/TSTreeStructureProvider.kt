package org.lukasj.idea.torquescript.editor

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import icons.TSIcons

class TSTreeStructureProvider : TreeStructureProvider {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): MutableCollection<AbstractTreeNode<*>> =
        children.onEach { it.icon = TSIcons.FILE }

    override fun getData(selected: MutableCollection<out AbstractTreeNode<*>>, dataId: String): Any? {
        return null
    }
}