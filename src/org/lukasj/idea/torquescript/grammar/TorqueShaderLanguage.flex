package org.lukasj.idea.torquescript.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import org.lukasj.idea.torquescript.psi.TslTypes;

%%

%class TslLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

DIGIT     = [0-9]
INTEGER   = {DIGIT}+
DOUBLE    = ({INTEGER}?\.{INTEGER})|({INTEGER}(\.{INTEGER})?[eE][+-]?{INTEGER})
FLOAT     = ({INTEGER}?\.{INTEGER})f
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
BLUEPRINT = [bB][lL][uU][eE][pP][rR][iI][nN][tT]
POSITION  = [pP][oO][sS][iI][tT][iI][oO][nN]
BINORMAL  = [bB][iI][nN][oO][rR][mM][aA][lL]
NORMAL    = [nN][oO][rR][mM][aA][lL]
COLOR     = [cC][oO][lL][oO][rR]
SV_POSITION = [sS][vV]_[pP][oO][sS][iI][tT][iI][oO][nN]
SV_TARGET = [sS][vV]_[tT][aA][rR][gG][eE][tT]
SV_DEPTH  = [sS][vV]_[dD][eE][pP][tT][hH]

TEXCOORD  = [tT][eE][xX][cC][oO][oO][rR][dD][0-9]

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
    {LINE_COMMENT}                                          { return TslTypes.LINE_COMMENT; }
    {MULTILINE_COMMENT}                                     { return TslTypes.BLOCK_COMMENT; }
// ----- KEYWORDS START -----
    {BLUEPRINT}                                             { return TslTypes.BLUEPRINT; }
    struct                                                  { return TslTypes.STRUCT; }
    VertData                                                { return TslTypes.STRUCT_VERTDATA; }
    ConnectData                                             { return TslTypes.STRUCT_CONNECTDATA; }
    FragOut                                                 { return TslTypes.STRUCT_FRAGOUT; }
    float                                                   { return TslTypes.FLOAT; }
    float2                                                  { return TslTypes.FLOAT2; }
    float3                                                  { return TslTypes.FLOAT3; }
    float4                                                  { return TslTypes.FLOAT4; }
    float2x2                                                { return TslTypes.FLOAT2X2; }
    float3x3                                                { return TslTypes.FLOAT3X3; }
    float4x3                                                { return TslTypes.FLOAT4X3; }
    float3x4                                                { return TslTypes.FLOAT3X4; }
    float4x4                                                { return TslTypes.FLOAT4X4; }
    vec2                                                    { return TslTypes.VEC2; }
    vec3                                                    { return TslTypes.VEC3; }
    vec4                                                    { return TslTypes.VEC4; }
    mat2x2                                                  { return TslTypes.MAT2X2; }
    mat3x3                                                  { return TslTypes.MAT3X3; }
    mat4x3                                                  { return TslTypes.MAT4X3; }
    mat3x4                                                  { return TslTypes.MAT3X4; }
    mat4x4                                                  { return TslTypes.MAT4X4; }
    int                                                     { return TslTypes.INT; }
    int2                                                    { return TslTypes.INT2; }
    int3                                                    { return TslTypes.INT3; }
    int4                                                    { return TslTypes.INT4; }
    {POSITION}                                              { return TslTypes.POSITION; }
    {BINORMAL}                                              { return TslTypes.BINORMAL; }
    {NORMAL}                                                { return TslTypes.NORMAL; }
    {COLOR}                                                 { return TslTypes.COLOR; }
    {SV_POSITION}                                           { return TslTypes.SV_POSITION; }
    {SV_TARGET}                                             { return TslTypes.SV_TARGET; }
    {SV_DEPTH}                                              { return TslTypes.SV_DEPTH; }
    {TEXCOORD}                                              { return TslTypes.TEXCOORD; }
    VertexShader                                            { return TslTypes.VERTEX_SHADER; }
    PixelShader                                             { return TslTypes.PIXEL_SHADER; }
    #pragma                                                 { return TslTypes.PRAGMA; }
    uniform                                                 { return TslTypes.UNIFORM; }
    sampler1D                                               { return TslTypes.SAMPLER1D; }
    sampler2D                                               { return TslTypes.SAMPLER2D; }
    sampler3D                                               { return TslTypes.SAMPLER3D; }
    sampler1DShadow                                         { return TslTypes.SAMPLER1DSHADOW; }
    sampler2DShadow                                         { return TslTypes.SAMPLER2DSHADOW; }
    sampler2DArray                                          { return TslTypes.SAMPLER2DARRAY; }
    samplerCube                                             { return TslTypes.SAMPLERCUBE; }
    samplerCubeArray                                        { return TslTypes.SAMPLERCUBEARRAY; }
    return                                                  { return TslTypes.RETURN; }
    if                                                      { return TslTypes.IF; }
    else                                                    { return TslTypes.ELSE; }
    switch                                                  { return TslTypes.SWITCH; }
    case                                                    { return TslTypes.CASE; }
    default                                                 { return TslTypes.DEFAULT; }
    while                                                   { return TslTypes.WHILE; }
    do                                                      { return TslTypes.DO; }
    for                                                     { return TslTypes.FOR; }
    break                                                   { return TslTypes.BREAK; }
    continue                                                { return TslTypes.CONTINUE; }
    discard                                                 { return TslTypes.DISCARD; }
    branch                                                  { return TslTypes.BRANCH; }
    flatten                                                 { return TslTypes.FLATTEN; }
    unroll                                                  { return TslTypes.UNROLL; }
    loop                                                    { return TslTypes.LOOP; }
    fastopt                                                 { return TslTypes.FASTOPT; }
    allow_uav_condition                                     { return TslTypes.ALLOW_UAV_CONDITION; }
    forcecase                                               { return TslTypes.FORCECASE; }
    call                                                    { return TslTypes.CALL; }
// ----- KEYWORDS END -----
// ----- PUNCTUATION START -----
    \(                                                      { return TslTypes.LPAREN; }
    \)                                                      { return TslTypes.RPAREN; }
    \{                                                      { return TslTypes.LBRACE; }
    \}                                                      { return TslTypes.RBRACE; }
    \[                                                      { return TslTypes.LBRACK; }
    \]                                                      { return TslTypes.RBRACK; }
    ;                                                       { return TslTypes.STMT_SEPARATOR; }
// ----- PUNCTUATION END -----
// ----- ASSIGNMENTS START -----
    =                                                       { return TslTypes.ASSIGN; }
    \+=                                                     { return TslTypes.ADD_ASSIGN; }
    -=                                                      { return TslTypes.SUBTRACT_ASSIGN; }
    \*=                                                     { return TslTypes.MULTIPLY_ASSIGN; }
    \/=                                                     { return TslTypes.DIVIDE_ASSIGN; }
    %=                                                      { return TslTypes.MODULO_ASSIGN; }
    &=                                                      { return TslTypes.BIT_AND_ASSIGN; }
    \^=                                                     { return TslTypes.BIT_XOR_ASSIGN; }
    \|=                                                     { return TslTypes.BIT_OR_ASSIGN; }
    \<\<=                                                   { return TslTypes.BIT_SHIFT_LEFT_ASSIGN; }
    >>=                                                     { return TslTypes.BIT_SHIFT_RIGHT_ASSIGN; }
// ----- ASSIGNMENTS END -----
// ----- OPERATORS START -----
    \.                                                      { return TslTypes.DOT; }
    ,                                                       { return TslTypes.COMMA; }
    ==                                                      { return TslTypes.EQUAL; }
    \!=                                                     { return TslTypes.NOT_EQUAL; }
    >=                                                      { return TslTypes.GT_EQUAL; }
    >                                                       { return TslTypes.GT; }
    \<=                                                     { return TslTypes.LT_EQUAL; }
    \<                                                      { return TslTypes.LT; }
    &&                                                      { return TslTypes.AND; }
    \|\|                                                    { return TslTypes.OR; }
    \!                                                      { return TslTypes.NOT; }
    \<<                                                     { return TslTypes.BIT_SHIFT_LEFT; }
    >>                                                      { return TslTypes.BIT_SHIFT_RIGHT; }
    &                                                       { return TslTypes.BIT_AND; }
    \|                                                      { return TslTypes.BIT_OR; }
    \^                                                      { return TslTypes.BIT_XOR; }
    \~                                                      { return TslTypes.BIT_NOT; }
    \+                                                      { return TslTypes.PLUS; }
    -                                                       { return TslTypes.MINUS; }
    \*                                                      { return TslTypes.MULTIPLY; }
    \/                                                      { return TslTypes.DIVIDE; }
    %                                                       { return TslTypes.MODULO; }
    --                                                      { return TslTypes.DECREMENT; }
    \+\+                                                    { return TslTypes.INCREMENT; }
    \?                                                      { return TslTypes.QUESTION_MARK; }
    :                                                       { return TslTypes.COLON; }
// ----- OPERATORS END -----
    {STRING}                                                { return TslTypes.QUOTED_STRING; }
    {INTEGER}                                               { return TslTypes.INTEGER; }
    {FLOAT}                                                 { return TslTypes.FLOAT; }
    {DOUBLE}                                                { return TslTypes.DOUBLE; }
    //0[xX]{HEXDIGIT}+                                        { return TslTypes.HEXDIGIT; }
    {ID}                                                    { return TslTypes.IDENT; }
    {ILID}                                                  { return TokenType.ERROR_ELEMENT; }
}

([\r\n]|{SPACE})+                                           { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }