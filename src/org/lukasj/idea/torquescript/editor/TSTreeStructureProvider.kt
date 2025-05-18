package org.lukasj.idea.torquescript.editor

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.actionSystem.DataSink
import icons.TSIcons

class TSTreeStructureProvider : TreeStructureProvider {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: MutableCollection<AbstractTreeNode<*>>,
        settings: ViewSettings?
    ): MutableCollection<AbstractTreeNode<*>> =
        children.onEach { it.icon = TSIcons.FILE }

    override fun uiDataSnapshot(sink: DataSink, selection: MutableCollection<out AbstractTreeNode<*>>) {
        super.uiDataSnapshot(sink, selection)
    }
}