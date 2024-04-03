package org.lukasj.idea.torquescript.editor

import com.intellij.lang.findUsages.FindUsagesProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import kotlinx.coroutines.selects.select

class TamlFindUsagesProvider : FindUsagesProvider {

    override fun canFindUsagesFor(psiElement: PsiElement) =
        psiElement is XmlAttribute && psiElement.name == "ModuleId"
                || psiElement is XmlTag && psiElement.name == "EngineClassType"

    override fun getHelpId(psiElement: PsiElement): String? = null

    override fun getType(element: PsiElement): String = "ModuleId"

    override fun getDescriptiveName(element: PsiElement): String = "ModuleId"

    override fun getNodeText(element: PsiElement, useFullName: Boolean): String =
        when (element) {
            is XmlAttribute -> element.value!!
            is XmlTag ->
                when (element.name) {
                    "EngineClassType" -> element.getAttributeValue("name")
                    "ModuleDefinition" -> element.getAttributeValue("ModuleId")
                    else -> ""
                } ?: ""
            else -> ""
        }
}