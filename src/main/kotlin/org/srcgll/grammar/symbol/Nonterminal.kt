package org.srcgll.grammar.symbol

import org.srcgll.grammar.RSMState

class Nonterminal <TerminalType>
(
   val value : String
)
    : Symbol
{
    lateinit var startState : RSMState<TerminalType>
    override fun toString() = "Nonterminal($value)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)           return true
        if (other !is Nonterminal<*>) return false
        if (value != other.value)     return false

        return true
    }

    val hashCode : Int = value.hashCode()
    override fun hashCode() = hashCode
}
