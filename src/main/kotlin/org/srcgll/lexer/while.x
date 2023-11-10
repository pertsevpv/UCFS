package org.srcgll.lexer;

import java.io.*;
import org.srcgll.lexer.Token;
import org.srcgll.lexer.SymbolCode;

%%

%public
%class GeneratedLexer
%type Token
%unicode

%{
    public Token token(SymbolCode tokenType)
    {
        return new Token<SymbolCode>(tokenType, yytext());
    }
%}

Space      = \ | \t | \n | \r | \r\n
Int        = [:digit:]+
Bool       = "true" | "false"
Id         = [:letter:]+
TextLimit  = "\""

%%

"if"        { return token(SymbolCode.IF); }
":="        { return token(SymbolCode.ASSIGN); }
"then"      { return token(SymbolCode.THEN); }
"else"      { return token(SymbolCode.ELSE); }
"skip"      { return token(SymbolCode.SKIP); }
"while"     { return token(SymbolCode.WHILE); }
"print"     { return token(SymbolCode.PRINT); }
"read"      { return token(SymbolCode.READ); }
"do"        { return token(SymbolCode.DO); }
"*"         { return token(SymbolCode.MULTIPLY); }
"/"         { return token(SymbolCode.DIVIDE); }
"+"         { return token(SymbolCode.PLUS); }
"-"         { return token(SymbolCode.MINUS); }
"not"       { return token(SymbolCode.NOT); }
"and"       { return token(SymbolCode.AND); }
"or"        { return token(SymbolCode.OR); }
"("         { return token(SymbolCode.LEFT); }
")"         { return token(SymbolCode.RIGHT); }
";"         { return token(SymbolCode.SEMICOLON); }
"{"         { return token(SymbolCode.LEFTCURLY); }
"}"         { return token(SymbolCode.RIGHTCURLY); }
"<"         { return token(SymbolCode.LESS); }
">"         { return token(SymbolCode.GREAT); }
"<="        { return token(SymbolCode.LESSOREQ); }
">="        { return token(SymbolCode.GREATOREQ); }
"="         { return token(SymbolCode.EQ); }
{Id}        { return token(SymbolCode.ID); }
{Int}       { return token(SymbolCode.INT); }
{Bool}      { return token(SymbolCode.BOOL); }
{TextLimit} { return token(SymbolCode.TEXTLIMIT); }
{Space}     {}
<<EOF>>     { return token(SymbolCode.EOF); }