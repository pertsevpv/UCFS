package org.kotgll.rsm.graphinput.sppf

import org.kotgll.graph.GraphNode
import org.kotgll.rsm.grammar.symbol.Terminal
import java.util.*

class TerminalSPPFNode
(
    leftExtent   : GraphNode,
    rightExtent  : GraphNode,
    val terminal : Terminal,
)
    : SPPFNode(leftExtent, rightExtent)
{
    override fun toString() =
        "TerminalSPPFNode(leftExtent=$leftExtent, rightExtent=$rightExtent, terminal=$terminal)"

    override fun equals(other : Any?) : Boolean
    {
        if (other !is TerminalSPPFNode) return false

        if (!super.equals(other))       return false

        return terminal == other.terminal
    }

    override val hashCode : Int = Objects.hash(leftExtent, rightExtent, terminal)
    override fun hashCode() = hashCode
}
