package org.lukasj.idea.torquescript.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import org.lukasj.idea.torquescript.psi.TSNamedElement

abstract class TSNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    TSNamedElement

