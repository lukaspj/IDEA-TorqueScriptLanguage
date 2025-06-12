package org.lukasj.idea.torquescript.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.lukasj.idea.torquescript.psi.TSTypes;

%%

%class TSLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

DIGIT     = [0-9]
INTEGER   = {DIGIT}+
FLOAT     = ({INTEGER}?\.{INTEGER})|({INTEGER}(\.{INTEGER})?[eE][+-]?{INTEGER})
LETTER    = [A-Za-z_]
FILECHAR  = [A-Za-z_\.]
VARMID    = [:A-Za-z0-9_]
IDTAIL    = [A-Za-z0-9_]
VARTAIL   = {VARMID}*{IDTAIL}
VAR       = {LETTER}{VARTAIL}*
THISVAR   = %this
LOCALVAR  = %{VAR}
GLOBALVAR = \${VAR}
ID        = {LETTER}{IDTAIL}*
ILID      = [$%]{DIGIT}+{LETTER}{VARTAIL}*
FILENAME  = {FILECHAR}+
SPACE     = [ \t\f]
HEXDIGIT  = [a-fA-F0-9]

DOC_COMMENT_BLOCK = ("///"([^/\n\r][^\n\r]*)?[\n\r]+)+
LINE_COMMENT = "//"[^\r\n]*
MULTILINE_COMMENT = "/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?

ESCAPES = [abfnrtv]|c[rpo0-9]
TAG =      "\'"
TAG_STRING = {TAG} ( [^\'\\\n\r] | "\\" ("\\" | {TAG} | {ESCAPES} | [0-8xuU] ) )* {TAG}?
STR =      "\""
STRING = {STR} ( [^\"\\\n\r] | "\\" ("\\" | {STR} | {ESCAPES} | [0-8xuU] ) )* {STR}?

%%

<YYINITIAL> {
    {DOC_COMMENT_BLOCK}                                     { return TSTypes.DOC_COMMENT_BLOCK; }
    {LINE_COMMENT}                                          { return TSTypes.LINE_COMMENT; }
    {MULTILINE_COMMENT}                                     { return TSTypes.BLOCK_COMMENT; }
// ----- KEYWORDS START -----
    new                                                     { return TSTypes.NEW; }
    if                                                      { return TSTypes.IF; }
    else                                                    { return TSTypes.ELSE; }
    switch                                                  { return TSTypes.SWITCH; }
    switch\$                                                { return TSTypes.STR_SWITCH; }
    do                                                      { return TSTypes.DO; }
    while                                                   { return TSTypes.WHILE; }
    for                                                     { return TSTypes.FOR; }
    foreach                                                 { return TSTypes.FOREACH; }
    foreach\$                                               { return TSTypes.STR_FOREACH; }
    in                                                      { return TSTypes.IN; }
    case                                                    { return TSTypes.CASE; }
    or                                                      { return TSTypes.CASEOR; }
    default                                                 { return TSTypes.DEFAULT; }
    break                                                   { return TSTypes.BREAK; }
    continue                                                { return TSTypes.CONTINUE; }
    assert                                                  { return TSTypes.ASSERT; }
    return                                                  { return TSTypes.RETURN; }
    function                                                { return TSTypes.FUNCTION; }
    datablock                                               { return TSTypes.DATABLOCK; }
    singleton                                               { return TSTypes.SINGLETON; }
    package                                                 { return TSTypes.PACKAGE; }
    namespace                                               { return TSTypes.NAMESPACE; }
    true                                                    { return TSTypes.TRUE; }
    false                                                   { return TSTypes.FALSE; }
    Parent                                                  { return TSTypes.PARENT; }
// ----- KEYWORDS END -----
// ----- PUNCTUATION START -----
    \(                                                      { return TSTypes.LPAREN; }
    \)                                                      { return TSTypes.RPAREN; }
    \{                                                      { return TSTypes.LBRACE; }
    \}                                                      { return TSTypes.RBRACE; }
    \[                                                      { return TSTypes.LBRACK; }
    \]                                                      { return TSTypes.RBRACK; }
    ;                                                       { return TSTypes.STMT_SEPARATOR; }
// ----- PUNCTUATION END -----
// ----- ASSIGNMENTS START -----
    =                                                       { return TSTypes.ASSIGN; }
    \+=                                                     { return TSTypes.ADD_ASSIGN; }
    -=                                                      { return TSTypes.SUBTRACT_ASSIGN; }
    \*=                                                     { return TSTypes.MULTIPLY_ASSIGN; }
    \/=                                                     { return TSTypes.DIVIDE_ASSIGN; }
    %=                                                      { return TSTypes.MODULO_ASSIGN; }
    &=                                                      { return TSTypes.BIT_AND_ASSIGN; }
    \^=                                                     { return TSTypes.BIT_XOR_ASSIGN; }
    \|=                                                     { return TSTypes.BIT_OR_ASSIGN; }
    \<\<=                                                   { return TSTypes.BIT_SHIFT_LEFT_ASSIGN; }
    >>=                                                     { return TSTypes.BIT_SHIFT_RIGHT_ASSIGN; }
// ----- ASSIGNMENTS END -----
// ----- OPERATORS START -----
    \.                                                      { return TSTypes.DOT; }
    ,                                                       { return TSTypes.COMMA; }
    ==                                                      { return TSTypes.EQUAL; }
    \!=                                                     { return TSTypes.NOT_EQUAL; }
    >=                                                      { return TSTypes.GT_EQUAL; }
    >                                                       { return TSTypes.GT; }
    \<=                                                     { return TSTypes.LT_EQUAL; }
    \<                                                      { return TSTypes.LT; }
    &&                                                      { return TSTypes.AND; }
    \|\|                                                    { return TSTypes.OR; }
    \!                                                      { return TSTypes.NOT; }
    \$=                                                     { return TSTypes.STR_EQUAL; }
    \!\$=                                                   { return TSTypes.STR_NOT_EQUAL; }
    \<<                                                     { return TSTypes.BIT_SHIFT_LEFT; }
    >>                                                      { return TSTypes.BIT_SHIFT_RIGHT; }
    &                                                       { return TSTypes.BIT_AND; }
    \|                                                      { return TSTypes.BIT_OR; }
    \^                                                      { return TSTypes.BIT_XOR; }
    \~                                                      { return TSTypes.BIT_NOT; }
    ::                                                      { return TSTypes.COLON_COLON; }
    ->                                                      { return TSTypes.INTERNAL_NAME; }
    -->                                                     { return TSTypes.INTERNAL_NAME_RECURSIVE; }
    @                                                       { return TSTypes.CONCATENATE; }
    \+                                                      { return TSTypes.PLUS; }
    -                                                       { return TSTypes.MINUS; }
    \*                                                      { return TSTypes.MULTIPLY; }
    \/                                                      { return TSTypes.DIVIDE; }
    %                                                       { return TSTypes.MODULO; }
    NL                                                      { return TSTypes.NL; }
    TAB                                                     { return TSTypes.TAB; }
    SPC                                                     { return TSTypes.SPC; }
    --                                                      { return TSTypes.DECREMENT; }
    \+\+                                                    { return TSTypes.INCREMENT; }
    \?                                                      { return TSTypes.QUESTION_MARK; }
    :                                                       { return TSTypes.COLON; }
// ----- OPERATORS END -----
    {STRING}                                                { return TSTypes.QUOTED_STRING; }
    {TAG_STRING}                                            { return TSTypes.TAGGED_STRING; }
    {INTEGER}                                               { return TSTypes.INTEGER; }
    {FLOAT}                                                 { return TSTypes.FLOAT; }
    0[xX]{HEXDIGIT}+                                        { return TSTypes.HEXDIGIT; }
    {THISVAR}                                               { return TSTypes.THISVAR; }
    {LOCALVAR}                                              { return TSTypes.LOCALVAR; }
    {GLOBALVAR}                                             { return TSTypes.GLOBALVAR; }
    {ID}                                                    { return TSTypes.IDENT; }
    {ILID}                                                  { return TokenType.ERROR_ELEMENT; }
}

([\r\n]|{SPACE})+                                           { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }