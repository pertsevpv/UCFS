package org.srcgll.lexer

import org.srcgll.grammar.symbol.ITerminal

class Token <TokenType>
(
    val type           : TokenType,
    override val value : String,
)
    : ITerminal
{
    override fun toString() : String
    {
        return "Token(${value},${type})"
    }

    val hashCode : Int = type.hashCode()
    override fun hashCode() = hashCode

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)            return true
        if (other !is Token<*>)        return false
        if (this.type != other.type)   return false

        return true
    }
}