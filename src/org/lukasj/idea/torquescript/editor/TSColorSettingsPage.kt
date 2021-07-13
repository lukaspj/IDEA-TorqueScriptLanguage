package org.lukasj.idea.torquescript.editor

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.lukasj.idea.torquescript.TSIcons
import javax.swing.Icon

class TSColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "TorqueScript"

    override fun getIcon(): Icon = TSIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = TSSyntaxHighlighter()

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
            AttributesDescriptor("Global variable", TSSyntaxHighlightingColors.GLOBALVAR),
            AttributesDescriptor("Local variable", TSSyntaxHighlightingColors.LOCALVAR),
            AttributesDescriptor("This variable", TSSyntaxHighlightingColors.THISVAR),
            AttributesDescriptor("Identifier", TSSyntaxHighlightingColors.IDENTIFIER),
            AttributesDescriptor("Function declaration", TSSyntaxHighlightingColors.FUNCTION_DECLARATION),
            AttributesDescriptor("Function call", TSSyntaxHighlightingColors.FUNCTION_CALL),
            AttributesDescriptor("Object name", TSSyntaxHighlightingColors.OBJECT_NAME),
            AttributesDescriptor("Class name", TSSyntaxHighlightingColors.CLASS_NAME),
            AttributesDescriptor("Number", TSSyntaxHighlightingColors.NUMBER),
            AttributesDescriptor("String", TSSyntaxHighlightingColors.STRING),
            AttributesDescriptor("Tag", TSSyntaxHighlightingColors.TAG),
            AttributesDescriptor("Keyword", TSSyntaxHighlightingColors.KEYWORD),
            AttributesDescriptor("Operator", TSSyntaxHighlightingColors.OPERATOR),
            AttributesDescriptor("Semicolon", TSSyntaxHighlightingColors.SEMICOLON),
            AttributesDescriptor("Dot", TSSyntaxHighlightingColors.DOT),
            AttributesDescriptor("Comma", TSSyntaxHighlightingColors.COMMA),
            AttributesDescriptor("Colon", TSSyntaxHighlightingColors.COLON),
            AttributesDescriptor("Line comment", TSSyntaxHighlightingColors.LINE_COMMENT),
            AttributesDescriptor("Doc comment", TSSyntaxHighlightingColors.DOC_COMMENT),
            AttributesDescriptor("Block comment", TSSyntaxHighlightingColors.BLOCK_COMMENT),
            AttributesDescriptor("Parentheses", TSSyntaxHighlightingColors.PARENTHESES),
            AttributesDescriptor("Braces", TSSyntaxHighlightingColors.BRACES),
            AttributesDescriptor("Brackets", TSSyntaxHighlightingColors.BRACKETS),
            AttributesDescriptor("Bad value", TSSyntaxHighlightingColors.BAD_CHARACTER)
        )
    }
}