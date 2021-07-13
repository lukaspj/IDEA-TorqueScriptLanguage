package org.lukasj.idea.torquescript.editor

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object TSSyntaxHighlightingColors {
    val FUNCTION_DECLARATION = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_FUNCTION_DECLARATION",
        DefaultLanguageHighlighterColors.FUNCTION_DECLARATION
    )
    val FUNCTION_CALL = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_FUNCTION_CALL",
        DefaultLanguageHighlighterColors.FUNCTION_CALL
    )
    val OBJECT_NAME = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_OBJECT_NAME",
        DefaultLanguageHighlighterColors.IDENTIFIER
    )
    val CLASS_NAME = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_CLASS_NAME",
        DefaultLanguageHighlighterColors.CLASS_NAME
    )
    val PARENTHESES = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_PARENTHESES",
        DefaultLanguageHighlighterColors.PARENTHESES
    )

    val BRACES = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_BRACES",
        DefaultLanguageHighlighterColors.BRACES
    )

    val BRACKETS = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_BRACKETS",
        DefaultLanguageHighlighterColors.BRACKETS
    )

    val LINE_COMMENT =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESCRIPT_LINE_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
        )

    val DOC_COMMENT =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESCRIPT_DOC_COMMENT",
            DefaultLanguageHighlighterColors.DOC_COMMENT
        )

    val BLOCK_COMMENT =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESCRIPT_BLOCK_COMMENT",
            DefaultLanguageHighlighterColors.BLOCK_COMMENT
        )

    val SEMICOLON = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_SEMICOLON",
        DefaultLanguageHighlighterColors.SEMICOLON
    )
    val DOT = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_DOT",
        DefaultLanguageHighlighterColors.DOT
    )
    val COMMA = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_COMMA",
        DefaultLanguageHighlighterColors.COMMA
    )
    val COLON = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_COLON",
        HighlighterColors.TEXT
    )
    val LOCALVAR = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_LOCAL_VAR",
        DefaultLanguageHighlighterColors.LOCAL_VARIABLE
    )
    val THISVAR = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_THIS_VAR",
        LOCALVAR
    )
    val GLOBALVAR = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_GLOBAL_VAR",
        DefaultLanguageHighlighterColors.GLOBAL_VARIABLE
    )
    val IDENTIFIER = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_IDENTIFIER",
        DefaultLanguageHighlighterColors.IDENTIFIER
    )
    val NUMBER = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_NUMBER",
        DefaultLanguageHighlighterColors.NUMBER
    )
    val STRING = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_STRING",
        DefaultLanguageHighlighterColors.STRING
    )
    val TAG = TextAttributesKey.createTextAttributesKey(
        "TORQUESCRIPT_TAG",
        DefaultLanguageHighlighterColors.LABEL
    )
    val KEYWORD =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESCRIPT_KEYWORD",
            DefaultLanguageHighlighterColors.KEYWORD
        )
    val OPERATOR =
        TextAttributesKey.createTextAttributesKey(
            "TORQUESCRIPT_OPERATOR",
            DefaultLanguageHighlighterColors.OPERATION_SIGN
        )
    val BAD_CHARACTER =
        TextAttributesKey.createTextAttributesKey("TORQUESCRIPT_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)
}