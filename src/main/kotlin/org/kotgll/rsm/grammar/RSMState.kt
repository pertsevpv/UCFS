package org.kotgll.rsm.grammar

import org.kotgll.rsm.grammar.symbol.Nonterminal
import org.kotgll.rsm.grammar.symbol.Terminal

open class RSMState(
    val id: Int,
    val nonterminal: Nonterminal,
    val isStart: Boolean = false,
    val isFinal: Boolean = false
) {
    val outgoingTerminalEdges: ArrayList<RSMTerminalEdge> = ArrayList()
    val outgoingNonterminalEdges: ArrayList<RSMNonterminalEdge> = ArrayList()

    override fun toString() =
        "RSMState(id=$id, nonterminal=$nonterminal, isStart=$isStart, isFinal=$isFinal)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RSMState) return false
        return id == other.id
    }

    val hashCode: Int = id
    override fun hashCode() = hashCode

    /**
     * Adds a new edge with the specified Terminal to the specified state
     */
    fun <T> addEdge(term: Terminal<T>, head: RSMState) {
        addTerminalEdge(RSMTerminalEdge(term, head))
    }

    /**
     * Adds a new edge with the specified Nonterminal to the specified state
     */
    fun addEdge(nonTerm: Nonterminal, head: RSMState) {
        addNonterminalEdge(RSMNonterminalEdge(nonTerm, head))
    }

    fun addTerminalEdge(edge: RSMTerminalEdge) {
        if (!outgoingTerminalEdges.contains(edge)) outgoingTerminalEdges.add(edge)
    }

    fun addNonterminalEdge(edge: RSMNonterminalEdge) {
        if (!outgoingNonterminalEdges.contains(edge)) outgoingNonterminalEdges.add(edge)
    }


}
