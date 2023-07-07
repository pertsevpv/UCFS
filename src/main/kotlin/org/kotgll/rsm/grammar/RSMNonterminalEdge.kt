package org.kotgll.rsm.grammar

import org.kotgll.rsm.grammar.symbol.Nonterminal

class RSMNonterminalEdge(val nonterminal : Nonterminal, val head : RSMState) : RSMEdge
{
    override fun toString() = "RSMNonterminalEdge(nonterminal=$nonterminal, head=$head)"

    override fun equals(other : Any?) : Boolean
    {
        if (other !is RSMNonterminalEdge)     return false

        if (nonterminal != other.nonterminal) return false

        if (this === other)                   return true

        return head == other.head
    }

    val hashCode : Int = nonterminal.hashCode()
    override fun hashCode() = hashCode
}
