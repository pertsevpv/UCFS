package org.srcgll.grammar

import org.srcgll.grammar.symbol.Terminal

class RSMTerminalEdge<TerminalType>
(
    val terminal : Terminal<TerminalType>,
    val head     : RSMState<TerminalType>,
)
{
    override fun toString() = "RSMTerminalEdge(terminal=$terminal, head=$head)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)               return true
        if (other !is RSMTerminalEdge<*>) return false
        if (terminal != other.terminal)   return false
        if (head != other.head)           return false

        return true
    }

    val hashCode : Int = terminal.hashCode()
    override fun hashCode() = hashCode
}
