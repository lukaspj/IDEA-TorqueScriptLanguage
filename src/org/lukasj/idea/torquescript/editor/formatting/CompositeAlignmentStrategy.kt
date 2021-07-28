package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.formatting.Alignment
import com.intellij.formatting.alignment.AlignmentStrategy
import com.intellij.psi.tree.IElementType

class AllAlignmentStrategy(val parentType: IElementType?, allowRightShift: Boolean) : AlignmentStrategy() {
    val _alignment: Alignment = Alignment.createAlignment(allowRightShift)
    override fun getAlignment(parentType: IElementType?, childType: IElementType?): Alignment? =
        if (this.parentType == null || this.parentType == parentType) {
            _alignment
        } else {
            null
        }
}

class CompositeAlignmentStrategy(private vararg val strategies: AlignmentStrategy) : AlignmentStrategy() {
    override fun getAlignment(parentType: IElementType?, childType: IElementType?) =
        strategies
            .mapNotNull { it.getAlignment(parentType, childType) }
            .firstOrNull()

    fun renewAlignment(type: IElementType) {
        strategies
            .also { array ->
                array.filterIsInstance<CompositeAlignmentStrategy>()
                    .forEach { it.renewAlignment(type) }
            }
            .filterIsInstance<AlignmentPerTypeStrategy>()
            .forEach { it.renewAlignment(type) }
    }

    fun withAlignmentPerTypeStrategy(
        childTypes: List<IElementType>,
        allowRightShift: Boolean
    ) =
        withAlignmentPerTypeStrategy(childTypes, null, allowRightShift)

    fun withAlignmentPerTypeStrategy(
        childTypes: List<IElementType>,
        parentType: IElementType?,
        allowRightShift: Boolean
    ) =
        if (childTypes.all { getAlignment(parentType, it) == null }) {
            CompositeAlignmentStrategy(
                this,
                createAlignmentPerTypeStrategy(
                    childTypes, parentType, allowRightShift
                )
            )
        } else {
            this
        }

    fun withAllAlignment(parentType: IElementType?, allowRightShift: Boolean) =
        if (getAlignment(parentType, null) == null) {
            CompositeAlignmentStrategy(
                this,
                AllAlignmentStrategy(parentType, allowRightShift)
            )
        } else {
            this
        }

    fun withNewAllAlignment(parentType: IElementType?, allowRightShift: Boolean) =
        CompositeAlignmentStrategy(
            AllAlignmentStrategy(parentType, allowRightShift)
        )

    fun withNewAllAlignmentIf(parentType: IElementType?, allowRightShift: Boolean, cond: Boolean) =
        if (cond) {
            withNewAllAlignment(parentType, allowRightShift)
        } else {
            withAllAlignment(parentType, allowRightShift)
        }
}