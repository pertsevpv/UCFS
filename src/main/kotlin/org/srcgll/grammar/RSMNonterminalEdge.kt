package org.srcgll.grammar

import org.srcgll.grammar.symbol.Nonterminal

class RSMNonterminalEdge <TerminalType>
(
    val nonterminal : Nonterminal<TerminalType>,
    val head        : RSMState<TerminalType>,
)
{
    override fun toString() = "RSMNonterminalEdge(nonterminal=$nonterminal, head=$head)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)                   return true
        if (other !is RSMNonterminalEdge<*>)  return false
        if (nonterminal != other.nonterminal) return false
        if (head != other.head)               return false

        return true
    }

    val hashCode : Int = nonterminal.hashCode()
    override fun hashCode() = hashCode
}
