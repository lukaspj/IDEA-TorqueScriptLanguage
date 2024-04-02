package org.lukasj.idea.torquescript.editor

import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute

class TamlFindUsagesProvider : FindUsagesProvider {

    override fun canFindUsagesFor(psiElement: PsiElement) =
        psiElement is XmlAttribute && psiElement.name == "ModuleId"

    override fun getHelpId(psiElement: PsiElement): String? = null

    override fun getType(element: PsiElement): String = "ModuleId"

    override fun getDescriptiveName(element: PsiElement): String = "ModuleId"

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        (element as XmlAttribute).value!!
}