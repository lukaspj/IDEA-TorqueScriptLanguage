package org.lukasj.idea.torquescript.editor

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import icons.TSIcons
import javax.swing.Icon

class TslColorSettingsPage : ColorSettingsPage {
    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "Torque Shader Language"

    override fun getIcon(): Icon = TSIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = TslSyntaxHighlighter()

    override fun getDemoText(): String = """
Blueprint "diffuse"
{
    struct VertData{
        float3 pos : POSITION;
        float2 texCoord : TEXCOORD0;
    };

    struct ConnectData{
        float4 hpos : SV_Position;
        float2 texCoord : TEXCOORD0;
    };

    struct FragOut{
        float4 col : SV_Target;
    };

    VertexShader
    {
        #pragma entry "main"
        
        uniform float4x4 modelview;

        ConnectData main(VertData IN)
        {
            ConnectData OUT;

            OUT.hpos = mul(modelview, float4(IN.pos, 1.0));
            OUT.texCoord = IN.texCoord;

            return OUT;
        };
    };

    PixelShader
    {
        #pragma entry "main"

        uniform sampler2D _inTex;

        FragOut main(ConnectData IN)
        {
            FragOut OUT;
            OUT.col = Sample(_inTex, IN.texCoord);
            return OUT;
        };
    };
};
"""

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null

    companion object {
        private val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Identifier", TslSyntaxHighlightingColors.IDENTIFIER),
            AttributesDescriptor("Function declaration", TslSyntaxHighlightingColors.FUNCTION_DECLARATION),
            AttributesDescriptor("Function call", TslSyntaxHighlightingColors.FUNCTION_CALL),
            AttributesDescriptor("Intrinsic function call", TslSyntaxHighlightingColors.INTRINSIC_FUNCTION_CALL),
            AttributesDescriptor("Semantic", TslSyntaxHighlightingColors.SEMANTIC),
            AttributesDescriptor("Struct type", TslSyntaxHighlightingColors.STRUCT_TYPES),
            AttributesDescriptor("Number", TslSyntaxHighlightingColors.NUMBER),
            AttributesDescriptor("String", TslSyntaxHighlightingColors.STRING),
            AttributesDescriptor("Keyword", TslSyntaxHighlightingColors.KEYWORD),
            AttributesDescriptor("Operator", TslSyntaxHighlightingColors.OPERATOR),
            AttributesDescriptor("Semicolon", TslSyntaxHighlightingColors.SEMICOLON),
            AttributesDescriptor("Dot", TslSyntaxHighlightingColors.DOT),
            AttributesDescriptor("Comma", TslSyntaxHighlightingColors.COMMA),
            AttributesDescriptor("Colon", TslSyntaxHighlightingColors.COLON),
            AttributesDescriptor("Line comment", TslSyntaxHighlightingColors.LINE_COMMENT),
            AttributesDescriptor("Block comment", TslSyntaxHighlightingColors.BLOCK_COMMENT),
            AttributesDescriptor("Parentheses", TslSyntaxHighlightingColors.PARENTHESES),
            AttributesDescriptor("Braces", TslSyntaxHighlightingColors.BRACES),
            AttributesDescriptor("Brackets", TslSyntaxHighlightingColors.BRACKETS),
            AttributesDescriptor("Bad value", TslSyntaxHighlightingColors.BAD_CHARACTER)
        )
    }
}