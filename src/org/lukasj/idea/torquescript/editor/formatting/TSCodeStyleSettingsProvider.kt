package org.lukasj.idea.torquescript.editor.formatting

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.psi.codeStyle.*
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.*
import com.intellij.psi.codeStyle.CommonCodeStyleSettings.END_OF_LINE
import org.lukasj.idea.torquescript.TSLanguage

class TSCodeStyleSettings(settings: CodeStyleSettings) :
    CustomCodeStyleSettings("TorqueScriptCodeStyleSettings", settings) {
    @JvmField
    @CommonCodeStyleSettings.BraceStyleConstant
    var PACKAGE_BRACE_STYLE = END_OF_LINE
}

class TSCodeStyleSettingsProvider : CodeStyleSettingsProvider() {
    override fun createCustomSettings(settings: CodeStyleSettings) = TSCodeStyleSettings(settings)

    override fun getConfigurableDisplayName() = "TorqueScript"

    override fun createConfigurable(
        settings: CodeStyleSettings,
        modelSettings: CodeStyleSettings
    ) = object : CodeStyleAbstractConfigurable(settings, modelSettings, configurableDisplayName) {
        override fun createPanel(settings: CodeStyleSettings) =
            TSCodeStyleMainPanel(currentSettings, settings)
    }
}

class TSCodeStyleMainPanel(currentSettings: CodeStyleSettings, settings: CodeStyleSettings) :
    TabbedLanguageCodeStylePanel(TSLanguage.INSTANCE, currentSettings, settings)

class TSLanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun getLanguage() = TSLanguage.INSTANCE

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        when (settingsType) {
            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> {
                consumer.showStandardOptions(
                    WrappingOrBraceOption.ALIGN_CONSECUTIVE_ASSIGNMENTS.name,
                    WrappingOrBraceOption.ALIGN_CONSECUTIVE_VARIABLE_DECLARATIONS.name,
                    WrappingOrBraceOption.ALIGN_MULTILINE_CHAINED_METHODS.name,
                    WrappingOrBraceOption.BRACE_STYLE.name,
                    WrappingOrBraceOption.CLASS_BRACE_STYLE.name,
                    WrappingOrBraceOption.METHOD_BRACE_STYLE.name,
                    WrappingOrBraceOption.ALIGN_MULTILINE_BINARY_OPERATION.name,
                )
                consumer.renameStandardOption(
                    WrappingOrBraceOption.CLASS_BRACE_STYLE.name,
                    "In object initializer"
                )
                consumer.showCustomOption(
                    TSCodeStyleSettings::class.java,
                    "PACKAGE_BRACE_STYLE",
                    "In package declaration",
                    CodeStyleSettingsCustomizableOptions.getInstance().WRAPPING_BRACES,
                    CodeStyleSettingsCustomizableOptions.getInstance().BRACE_PLACEMENT_OPTIONS,
                    BRACE_PLACEMENT_VALUES,
                )
            }
            SettingsType.SPACING_SETTINGS -> {
                consumer.showStandardOptions(
                    SpacingOption.SPACE_AROUND_ASSIGNMENT_OPERATORS.name,
                    SpacingOption.SPACE_AROUND_ADDITIVE_OPERATORS.name,
                    SpacingOption.SPACE_AROUND_BITWISE_OPERATORS.name,
                    SpacingOption.SPACE_AROUND_MULTIPLICATIVE_OPERATORS.name,
                    SpacingOption.SPACE_AROUND_RELATIONAL_OPERATORS.name,
                    SpacingOption.SPACE_AROUND_EQUALITY_OPERATORS.name,
                    SpacingOption.SPACE_AROUND_LOGICAL_OPERATORS.name,
                    SpacingOption.SPACE_AROUND_SHIFT_OPERATORS.name,
                    SpacingOption.SPACE_AROUND_UNARY_OPERATOR.name,
                    SpacingOption.SPACE_BEFORE_COMMA.name,
                    SpacingOption.SPACE_AFTER_COMMA.name,
                    SpacingOption.SPACE_BEFORE_COLON.name,
                    SpacingOption.SPACE_AFTER_COLON.name,
                    SpacingOption.SPACE_BEFORE_ELSE_KEYWORD.name,
                    SpacingOption.SPACE_BEFORE_ELSE_LBRACE.name,
                    SpacingOption.SPACE_BEFORE_IF_PARENTHESES.name,
                    SpacingOption.SPACE_BEFORE_CLASS_LBRACE.name,
                    SpacingOption.SPACE_BEFORE_COLON.name,
                    SpacingOption.SPACE_AFTER_COLON.name,
                )
            }
            SettingsType.BLANK_LINES_SETTINGS -> {
                consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE")
            }
            SettingsType.INDENT_SETTINGS -> {
                consumer.showStandardOptions(
                    IndentOption.INDENT_SIZE.name,
                    IndentOption.CONTINUATION_INDENT_SIZE.name,
                    IndentOption.USE_TAB_CHARACTER.name,
                    IndentOption.TAB_SIZE.name,
                    IndentOption.KEEP_INDENTS_ON_EMPTY_LINES.name,
                    IndentOption.SMART_TABS.name,
                    IndentOption.USE_RELATIVE_INDENTS.name,
                )
            }
            SettingsType.COMMENTER_SETTINGS -> {
            }
            SettingsType.LANGUAGE_SPECIFIC -> {
            }
        }
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor {
        return SmartIndentOptionsEditor(this)
    }

    override fun getCodeSample(settingsType: SettingsType) =
        when (settingsType) {
            SettingsType.WRAPPING_AND_BRACES_SETTINGS ->
                this::class.java.getResource("/samples/settings.wrapping-and-braces.tscript")
                    ?.readText()
            else ->
                this::class.java.getResource("/samples/settings.default.tscript")
                    ?.readText()
        } ?: "Sample not found"
}