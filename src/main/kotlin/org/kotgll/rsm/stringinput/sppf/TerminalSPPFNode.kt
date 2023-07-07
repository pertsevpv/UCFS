package org.kotgll.rsm.stringinput.sppf

import org.kotgll.rsm.grammar.symbol.Terminal
import java.util.*

class TerminalSPPFNode
(
    leftExtent   : Int,
    rightExtent  : Int,
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
