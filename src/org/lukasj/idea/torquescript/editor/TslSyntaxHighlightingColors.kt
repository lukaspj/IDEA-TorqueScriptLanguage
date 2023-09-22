package org.lukasj.idea.torquescript.editor

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object TslSyntaxHighlightingColors {
    val FUNCTION_DECLARATION = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_FUNCTION_DECLARATION",
        DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
    )
    val FUNCTION_CALL = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_FUNCTION_CALL",
        DefaultLanguageHighlighterColors.FUNCTION_CALL
    )
    val INTRINSIC_FUNCTION_CALL = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_BUILTIN_FUNCTION_CALL",
        DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL
    )
    val SEMANTIC = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_SEMANTIC",
        DefaultLanguageHighlighterColors.CONSTANT
    )
    val STRUCT_TYPES = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_STRUCT_TYPES",
        DefaultLanguageHighlighterColors.CLASS_NAME
    )
    val PARENTHESES = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_PARENTHESES",
        DefaultLanguageHighlighterColors.PARENTHESES
    )

    val BRACES = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_BRACES",
        DefaultLanguageHighlighterColors.BRACES
    )

    val BRACKETS = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_BRACKETS",
        DefaultLanguageHighlighterColors.BRACKETS
    )

    val LINE_COMMENT =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESHADERLANGUAGE_LINE_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )

    val BLOCK_COMMENT =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESHADERLANGUAGE_BLOCK_COMMENT",
            DefaultLanguageHighlighterColors.BLOCK_COMMENT
        )

    val SEMICOLON = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_SEMICOLON",
        DefaultLanguageHighlighterColors.SEMICOLON
    )
    val DOT = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_DOT",
        DefaultLanguageHighlighterColors.DOT
    )
    val COMMA = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_COMMA",
        DefaultLanguageHighlighterColors.COMMA
    )
    val COLON = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_COLON",
        HighlighterColors.TEXT
    )
    val IDENTIFIER = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_IDENTIFIER",
        DefaultLanguageHighlighterColors.IDENTIFIER
    )
    val NUMBER = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_NUMBER",
        DefaultLanguageHighlighterColors.NUMBER
    )
    val STRING = TextAttributesKey.createTextAttributesKey(
        "TORQUESHADERLANGUAGE_STRING",
        DefaultLanguageHighlighterColors.STRING
    )
    val KEYWORD =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESHADERLANGUAGE_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )
    val OPERATOR =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESHADERLANGUAGE_OPERATOR",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
    val ASSIGNMENT_OPERATOR =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESHADERLANGUAGE_ASSIGNMENT_OPERATOR",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
    val BAD_CHARACTER =
        TextAttributesKey.createTextAttributesKey("TORQUESHADERLANGUAGE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
}