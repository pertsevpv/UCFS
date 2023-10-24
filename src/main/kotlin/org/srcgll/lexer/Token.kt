package org.srcgll.lexer

class Token<TokenType>
(
    val type  : TokenType,
    val value : String,
)
{
    override fun toString() : String {
        return "Token(${value},${type})"
    }
}