package org.lukasj.idea.torquescript.editor

import com.intellij.lang.Commenter

class TSCommenter : Commenter {
    override fun getLineCommentPrefix() = "//"

    override fun getBlockCommentPrefix() = "/*"

    override fun getBlockCommentSuffix() = "*/"

    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null
}