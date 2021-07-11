package org.lukasj.idea.torquescript.editor

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.lukasj.idea.torquescript.TorqueScriptIcons
import javax.swing.Icon

class TorqueScriptColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "TorqueScript"

    override fun getIcon(): Icon = TorqueScriptIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = TorqueScriptSyntaxHighlighter()

    override fun getDemoText(): String = """// asd

%qwe["${"$"}asd] = "asd";
%qwe.asd = "asd";

"${"$"}minion = new AIPlayer(Fubar) {
   datablock = MinionData;
};
%a = "asd";
%v = 'asd';

"${"$"}minion.setMoveDestination("50 0 0");

{
    %a = 2;
}

%a = 1;
%a -= 0x213;
%a += 1;
%a *= 1;
%a /= 1;
%a %= 1;
%a &= 1;
%a ^= 1;
%a |= 1;
%a <<= 1;
%a >>= 1;

datablock ASD(QWE) {
    qwe = zxc;
    vxa = "qwe";
    vda = 21;
}

singleton lcks(lal) {

}

package A {
    function foo(%a) {
        return echo(%a);
    }
}

function ASD(%this, %qwe) {
    %asd = 2;
    return 4;
}
"""

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Global variable", TorqueScriptSyntaxHighlightingColors.GLOBALVAR),
            AttributesDescriptor("Local variable", TorqueScriptSyntaxHighlightingColors.LOCALVAR),
            AttributesDescriptor("This variable", TorqueScriptSyntaxHighlightingColors.THISVAR),
            AttributesDescriptor("Identifier", TorqueScriptSyntaxHighlightingColors.IDENTIFIER),
            AttributesDescriptor("Number", TorqueScriptSyntaxHighlightingColors.NUMBER),
            AttributesDescriptor("String", TorqueScriptSyntaxHighlightingColors.STRING),
            AttributesDescriptor("Tag", TorqueScriptSyntaxHighlightingColors.TAG),
            AttributesDescriptor("Keyword", TorqueScriptSyntaxHighlightingColors.KEYWORD),
            AttributesDescriptor("Operator", TorqueScriptSyntaxHighlightingColors.OPERATOR),
            AttributesDescriptor("Semicolon", TorqueScriptSyntaxHighlightingColors.SEMICOLON),
            AttributesDescriptor("Dot", TorqueScriptSyntaxHighlightingColors.DOT),
            AttributesDescriptor("Comma", TorqueScriptSyntaxHighlightingColors.COMMA),
            AttributesDescriptor("Colon", TorqueScriptSyntaxHighlightingColors.COLON),
            AttributesDescriptor("Line comment", TorqueScriptSyntaxHighlightingColors.LINE_COMMENT),
            AttributesDescriptor("Doc comment", TorqueScriptSyntaxHighlightingColors.DOC_COMMENT),
            AttributesDescriptor("Block comment", TorqueScriptSyntaxHighlightingColors.BLOCK_COMMENT),
            AttributesDescriptor("Parentheses", TorqueScriptSyntaxHighlightingColors.PARENTHESES),
            AttributesDescriptor("Braces", TorqueScriptSyntaxHighlightingColors.BRACES),
            AttributesDescriptor("Brackets", TorqueScriptSyntaxHighlightingColors.BRACKETS),
            AttributesDescriptor("Bad value", TorqueScriptSyntaxHighlightingColors.BAD_CHARACTER)
        )
    }
}