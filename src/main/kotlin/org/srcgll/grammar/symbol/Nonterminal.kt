package org.srcgll.grammar.symbol

import org.srcgll.grammar.RSMState

class Nonterminal
(
   val value : String
)
{
    lateinit var startState : RSMState
    override fun toString() = "Nonterminal($value)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)        return true

        if (other !is Nonterminal) return false

        if (value != other.value)    return false

        return true
    }

    val hashCode : Int = value.hashCode()
    override fun hashCode() = hashCode
}
