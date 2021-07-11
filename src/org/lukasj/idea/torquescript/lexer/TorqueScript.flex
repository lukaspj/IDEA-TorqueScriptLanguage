package org.lukasj.idea.torquescript.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.intellij.grammar.livePreview.LivePreviewElementType;import org.lukasj.idea.torquescript.psi.TorqueScriptTypes;

%%

%class TorqueScriptLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%{
    StringBuffer string = new StringBuffer();
%}

DIGIT    = [0-9]
INTEGER  = {DIGIT}*
FLOAT    = ({INTEGER}?\.{INTEGER})|({INTEGER}(\.{INTEGER})?[eE][+-]?{INTEGER})
LETTER   = [A-Za-z_]
FILECHAR = [A-Za-z_\.]
VARMID   = [:A-Za-z0-9_]
IDTAIL   = [A-Za-z0-9_]
VARTAIL  = {VARMID}*{IDTAIL}
VAR      = {LETTER}{VARTAIL}*
THISVAR = %this
LOCALVAR = %{VAR}
GLOBALVAR = \${VAR}
ID       = {LETTER}{IDTAIL}*
ILID     = [$%]{DIGIT}+{LETTER}{VARTAIL}*
FILENAME = {FILECHAR}+
SPACE    = [ \t\v\f]
HEXDIGIT = [a-fA-F0-9]

LINE_COMMENT = "//"[^\r\n]*
MULTILINE_COMMENT = "/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?

TAG =      "\'"
TAG_STRING = {TAG} ( [^\'\\\n\r] | "\\" ("\\" | {TAG} | {ESCAPES} | [0-8xuU] ) )* {TAG}?
STR =      "\""
STRING = {STR} ( [^\"\\\n\r] | "\\" ("\\" | {STR} | {ESCAPES} | [0-8xuU] ) )* {STR}?
ESCAPES = [abfnrtv]

%%

<YYINITIAL> {
    {LINE_COMMENT}                                          { return TorqueScriptTypes.LINE_COMMENT; }
    {MULTILINE_COMMENT}                                     { return TorqueScriptTypes.BLOCK_COMMENT; }
// ----- KEYWORDS START -----
    new                                                     { return TorqueScriptTypes.NEW; }
    if                                                      { return TorqueScriptTypes.IF; }
    switch                                                  { return TorqueScriptTypes.SWITCH; }
    do                                                      { return TorqueScriptTypes.DO; }
    while                                                   { return TorqueScriptTypes.WHILE; }
    for                                                     { return TorqueScriptTypes.FOR; }
    foreach                                                 { return TorqueScriptTypes.FOREACH; }
    foreach\$                                               { return TorqueScriptTypes.STR_FOREACH; }
    in                                                      { return TorqueScriptTypes.IN; }
    break                                                   { return TorqueScriptTypes.BREAK; }
    continue                                                { return TorqueScriptTypes.CONTINUE; }
    assert                                                  { return TorqueScriptTypes.ASSERT; }
    return                                                  { return TorqueScriptTypes.RETURN; }
    function                                                { return TorqueScriptTypes.FUNCTION; }
    datablock                                               { return TorqueScriptTypes.DATABLOCK; }
    singleton                                               { return TorqueScriptTypes.SINGLETON; }
    package                                                 { return TorqueScriptTypes.PACKAGE; }
    namespace                                               { return TorqueScriptTypes.NAMESPACE; }
    true                                                    { return TorqueScriptTypes.TRUE; }
    false                                                   { return TorqueScriptTypes.FALSE; }
// ----- KEYWORDS END -----
// ----- PUNCTUATION START -----
    \(                                                      { return TorqueScriptTypes.LEFT_PAREN; }
    \)                                                      { return TorqueScriptTypes.RIGHT_PAREN; }
    \{                                                      { return TorqueScriptTypes.LBRACE; }
    \}                                                      { return TorqueScriptTypes.RBRACE; }
    \[                                                      { return TorqueScriptTypes.LEFT_BRACK; }
    \]                                                      { return TorqueScriptTypes.RIGHT_BRACK; }
    ;                                                       { return TorqueScriptTypes.STMT_SEPARATOR; }
// ----- PUNCTUATION END -----
// ----- ASSIGNMENTS START -----
    =                                                       { return TorqueScriptTypes.ASSIGN; }
    \+=                                                     { return TorqueScriptTypes.ADD_ASSIGN; }
    -=                                                      { return TorqueScriptTypes.SUBTRACT_ASSIGN; }
    \*=                                                     { return TorqueScriptTypes.MULTIPLY_ASSIGN; }
    \/=                                                     { return TorqueScriptTypes.DIVIDE_ASSIGN; }
    %=                                                      { return TorqueScriptTypes.MODULO_ASSIGN; }
    &=                                                      { return TorqueScriptTypes.BIT_AND_ASSIGN; }
    \^=                                                     { return TorqueScriptTypes.BIT_XOR_ASSIGN; }
    \|=                                                     { return TorqueScriptTypes.BIT_OR_ASSIGN; }
    \<\<=                                                   { return TorqueScriptTypes.BIT_SHIFT_LEFT_ASSIGN; }
    >>=                                                     { return TorqueScriptTypes.BIT_SHIFT_RIGHT_ASSIGN; }
// ----- ASSIGNMENTS END -----
// ----- OPERATORS START -----
    \.                                                      { return TorqueScriptTypes.DOT; }
    ,                                                       { return TorqueScriptTypes.COMMA; }
    ==                                                      { return TorqueScriptTypes.EQUAL; }
    \!=                                                     { return TorqueScriptTypes.NOT_EQUAL; }
    >=                                                      { return TorqueScriptTypes.GT_EQUAL; }
    >                                                       { return TorqueScriptTypes.GT; }
    \<=                                                     { return TorqueScriptTypes.LT_EQUAL; }
    \<                                                      { return TorqueScriptTypes.LT; }
    &&                                                      { return TorqueScriptTypes.AND; }
    \|\|                                                    { return TorqueScriptTypes.OR; }
    \!                                                      { return TorqueScriptTypes.NOT; }
    \$=                                                     { return TorqueScriptTypes.STR_EQUAL; }
    \!\$=                                                   { return TorqueScriptTypes.STR_NOT_EQUAL; }
    \<<                                                     { return TorqueScriptTypes.BIT_SHIFT_LEFT; }
    >>                                                      { return TorqueScriptTypes.BIT_SHIFT_RIGHT; }
    &                                                       { return TorqueScriptTypes.BIT_AND; }
    \|                                                      { return TorqueScriptTypes.BIT_OR; }
    \^                                                      { return TorqueScriptTypes.BIT_XOR; }
    \~                                                      { return TorqueScriptTypes.BIT_NOT; }
    ::                                                      { return TorqueScriptTypes.COLON_COLON; }
    ->                                                      { return TorqueScriptTypes.INTERNAL_NAME; }
    -->                                                     { return TorqueScriptTypes.INTERNAL_NAME_RECURSIVE; }
    @                                                       { return TorqueScriptTypes.CONCATENATE; }
    \+                                                      { return TorqueScriptTypes.PLUS; }
    -                                                       { return TorqueScriptTypes.MINUS; }
    \*                                                      { return TorqueScriptTypes.MULTIPLY; }
    \/                                                      { return TorqueScriptTypes.DIVIDE; }
    NL                                                      { return TorqueScriptTypes.NL; }
    TAB                                                     { return TorqueScriptTypes.TAB; }
    SPC                                                     { return TorqueScriptTypes.SPC; }
    --                                                      { return TorqueScriptTypes.DECREMENT; }
    \+\+                                                    { return TorqueScriptTypes.INCREMENT; }
    \?                                                      { return TorqueScriptTypes.QUESTION_MARK; }
    :                                                       { return TorqueScriptTypes.COLON; }
// ----- OPERATORS END -----
    {STRING}                                                { return TorqueScriptTypes.QUOTED_STRING; }
    {TAG_STRING}                                            { return TorqueScriptTypes.TAGGED_STRING; }
    {INTEGER}                                               { return TorqueScriptTypes.INTEGER; }
    {FLOAT}                                                 { return TorqueScriptTypes.FLOAT; }
    0[xX]{HEXDIGIT}+                                        { return TorqueScriptTypes.HEXDIGIT; }
    {THISVAR}                                               { return TorqueScriptTypes.THISVAR; }
    {LOCALVAR}                                              { return TorqueScriptTypes.LOCALVAR; }
    {GLOBALVAR}                                             { return TorqueScriptTypes.GLOBALVAR; }
    {ID}                                                    { return TorqueScriptTypes.IDENT; }
    {ILID}                                                  { return TokenType.ERROR_ELEMENT; }
}

([\r\n]|{SPACE})+                                           { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }