package org.srcgll.lexer

import org.srcgll.grammar.symbol.ITerminal

class Token <TokenType>
(
    val type  : TokenType,
    override val value : String,
)
    : ITerminal
{
    override fun toString() : String {
        return "Token(${value},${type})"
    }
    override fun match(pos : Any, str : String) = (value == str)
}