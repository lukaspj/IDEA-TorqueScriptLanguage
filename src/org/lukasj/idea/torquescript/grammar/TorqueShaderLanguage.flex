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
FLOAT     = ({INTEGER}?\.{INTEGER})[fF]
LETTER    = [A-Za-z_]
FILECHAR  = [A-Za-z_\.]
IDTAIL    = [A-Za-z0-9_]
VAR       = {LETTER}{IDTAIL}*
ID        = {LETTER}{IDTAIL}*
ILID      = {DIGIT}{IDTAIL}*
SPACE     = [ \t\v\f]
HEXDIGIT  = [a-fA-F0-9]

/*
BLUEPRINT = [bB][lL][uU][eE][pP][rR][iI][nN][tT]
POSITION  = [pP][oO][sS][iI][tT][iI][oO][nN]
BINORMAL  = [bB][iI][nN][oO][rR][mM][aA][lL]
NORMAL    = [nN][oO][rR][mM][aA][lL]
COLOR     = [cC][oO][lL][oO][rR]
SV_POSITION = [sS][vV]_[pP][oO][sS][iI][tT][iI][oO][nN]
SV_TARGET = [sS][vV]_[tT][aA][rR][gG][eE][tT]
SV_DEPTH  = [sS][vV]_[dD][eE][pP][tT][hH]

TEXCOORD  = [tT][eE][xX][cC][oO][oO][rR][dD][0-9]
 */

DOC_COMMENT_BLOCK = ("///"([^/\n\r][^\n\r]*)?[\n\r]+)+
LINE_COMMENT = "//"[^\r\n]*
MULTILINE_COMMENT = "/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?

ESCAPES = [abfnrtv]|c[rpo0-9]
STR =      "\""
STRING = {STR} (\\.|[^\"\\\n\r])* {STR}?

%%

<YYINITIAL> {
    {LINE_COMMENT}                                          { return TslTypes.LINE_COMMENT; }
    {MULTILINE_COMMENT}                                     { return TslTypes.BLOCK_COMMENT; }
// ----- SEMANTICS START -----
    BINORMAL[0-9]*                                          { return TslTypes.BINORMAL; }
    NORMAL[0-9]*                                            { return TslTypes.NORMAL; }
    TANGENT[0-9]*                                           { return TslTypes.TANGENT; }
    TANGENTW[0-9]*                                          { return TslTypes.TANGENTW; }
    COLOR[0-9]*                                             { return TslTypes.COLOR; }
    TARGET[0-9]*                                            { return TslTypes.TARGET; }
    POSITION[0-9]*                                          { return TslTypes.POSITION; }
    SV_POSITION[0-9]*                                       { return TslTypes.SV_POSITION; }
    // SV_TARGET[0-9]*                                         { return TslTypes.SV_TARGET; }
    SV_DEPTH[0-9]*                                          { return TslTypes.SV_DEPTH; }
    SV_ISFRONTFACE                                          { return TslTypes.SV_ISFRONTFACE; }
    TEXCOORD[0-9]*                                          { return TslTypes.TEXCOORD; }
    PSIZE                                                   { return TslTypes.PSIZE; }
    TESSFACTOR[0-9]*                                        { return TslTypes.TESSFACTOR; }
// ----- SEMANTICS END -----
// ----- INTRINSIC FUNCTIONS START -----
    Sample                                                  { return TslTypes.SAMPLEFUNC; }
    mul                                                     { return TslTypes.MULFUNC; }
    frac|fract                                              { return TslTypes.FRACFUNC; }
    lerp|mix                                                { return TslTypes.LERPFUNC; }
// ----- INTRINSIC FUNCTIONS END -----
// ----- KEYWORDS START -----
    TorqueShader                                            { return TslTypes.TORQUESHADER; }
    struct                                                  { return TslTypes.STRUCT; }
    uniform                                                 { return TslTypes.UNIFORM; }
    // cbuffer                                                 { return TslTypes.CBUFFER; }
    VertexShader                                            { return TslTypes.VERTEX_SHADER; }
    PixelShader                                             { return TslTypes.PIXEL_SHADER; }
    GeometryShader                                          { return TslTypes.GEOMETRY_SHADER; }
    ComputeShader                                           { return TslTypes.COMPUTE_SHADER; }
    // DomainShader                                            { return TslTypes.DOMAIN_SHADER; }
    // HullShader                                              { return TslTypes.HULL_SHADER; }
    float3x4|mat3x4                                         { return TslTypes.MAT3X4; }
    float4x3|mat4x3                                         { return TslTypes.MAT4X3; }
    float3x3|mat3|mat3x3                                    { return TslTypes.MAT3X3; }
    float4x4|mat4|mat4x4                                    { return TslTypes.MAT4X4; }
    float2|vec2                                             { return TslTypes.FVEC2; }
    float3|vec3                                             { return TslTypes.FVEC3; }
    float4|vec4                                             { return TslTypes.FVEC4; }
    int2|ivec2                                              { return TslTypes.IVEC2; }
    int3|ivec3                                              { return TslTypes.IVEC3; }
    int4|ivec4                                              { return TslTypes.IVEC4; }
    bool2|bvec2                                             { return TslTypes.BVEC2; }
    bool3|bvec3                                             { return TslTypes.BVEC3; }
    bool4|bvec4                                             { return TslTypes.BVEC4; }
    float                                                   { return TslTypes.FLOAT; }
    int                                                     { return TslTypes.INT; }
    uint                                                    { return TslTypes.UINT; }
    bool                                                    { return TslTypes.BOOL; }
    sampler2D                                               { return TslTypes.SAMPLER2D; }
    if                                                      { return TslTypes.IF; }
    else                                                    { return TslTypes.ELSE; }
    while                                                   { return TslTypes.WHILE; }
    do                                                      { return TslTypes.DO; }
    break                                                   { return TslTypes.BREAK; }
    // for                                                     { return TslTypes.FOR; }
    switch                                                  { return TslTypes.SWITCH; }
    case                                                    { return TslTypes.CASE; }
    default                                                 { return TslTypes.DEFAULT; }
    continue                                                { return TslTypes.CONTINUE; }
    discard                                                 { return TslTypes.DISCARD; }
    void                                                    { return TslTypes.VOID; }
    static                                                  { return TslTypes.STATIC; }
    const                                                   { return TslTypes.CONST; }
    in                                                      { return TslTypes.IN; }
    out                                                     { return TslTypes.OUT; }
    inout                                                   { return TslTypes.INOUT; }
    // typedef                                                 { return TslTypes.TYPEDEF; }
    true                                                    { return TslTypes.TRUE; }
    false                                                   { return TslTypes.FALSE; }
    return                                                  { return TslTypes.RETURN; }

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
    // \.                                                      { return TslTypes.DOT; }
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
    // \?                                                      { return TslTypes.QUESTION_MARK; }
    :                                                       { return TslTypes.COLON; }
// ----- OPERATORS END -----
    {INTEGER}                                               { return TslTypes.LITERAL_INTEGER; }
    {FLOAT}                                                 { return TslTypes.LITERAL_FLOAT; }
    {DOUBLE}                                                { return TslTypes.LITERAL_DOUBLE; }
    0[xX]{HEXDIGIT}+                                        { return TslTypes.LITERAL_HEXDIGIT; }
    \.{ID}                                                  { return TslTypes.MEMBER_VAR; }
    {ID}                                                    { return TslTypes.IDENT; }
    // {STRING}                                                { return TslTypes.STRING; }
    {ILID}                                                  { return TokenType.ERROR_ELEMENT; }
}

([\r\n]|{SPACE})+                                           { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }