package org.srcgll.grammar

import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Terminal

class RSMState
(
    val id          : Int,
    val nonterminal : Nonterminal,
    val isStart     : Boolean = false,
    val isFinal     : Boolean = false,
)
{
    val errorRecoveryLabels      = HashSet<Terminal>()
    val coveredTargetStates      = HashSet<RSMState>()
    val outgoingTerminalEdges    = HashMap<Terminal, HashSet<RSMState>>()
    val outgoingNonterminalEdges = HashMap<Nonterminal, HashSet<RSMState>>()

    override fun toString() =
        "RSMState(id=$id, nonterminal=$nonterminal, isStart=$isStart, isFinal=$isFinal)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)     return true

        if (other !is RSMState) return false

        if (id != other.id)     return false

        return true
    }

    val hashCode : Int = id
    override fun hashCode() = hashCode

    fun addTerminalEdge(edge : RSMTerminalEdge)
    {
        if (!coveredTargetStates.contains(edge.head)) {
            var added = errorRecoveryLabels.add(edge.terminal)
            assert(added)
            added = coveredTargetStates.add(edge.head)
            assert(added)
        }
        
        if (outgoingTerminalEdges.containsKey(edge.terminal)) {
            val targetStates = outgoingTerminalEdges.getValue(edge.terminal)
            targetStates.add(edge.head)
        } else {
            outgoingTerminalEdges[edge.terminal] = hashSetOf(edge.head)
        }
    }

    fun addNonterminalEdge(edge : RSMNonterminalEdge)
    {
        if (outgoingNonterminalEdges.containsKey(edge.nonterminal)) {
            val targetStates = outgoingNonterminalEdges.getValue(edge.nonterminal)
            targetStates.add(edge.head)
        } else {
            outgoingNonterminalEdges[edge.nonterminal] = hashSetOf(edge.head)
        }
    }
}
