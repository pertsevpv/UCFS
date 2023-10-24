package org.srcgll.lexer

import org.srcgll.grammar.symbol.Terminal

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