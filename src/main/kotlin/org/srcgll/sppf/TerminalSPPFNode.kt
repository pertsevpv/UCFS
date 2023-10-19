package org.srcgll.sppf

import org.srcgll.grammar.TokenSequence
import org.srcgll.grammar.symbol.Terminal
import java.util.*

class TerminalSPPFNode
(
    val terminal : Terminal?,
    leftExtent   : TokenSequence,
    rightExtent  : TokenSequence,
    weight       : Int,
)
    : SPPFNode(leftExtent, rightExtent, weight)
{
    override fun toString() =
        "TerminalSPPFNode(leftExtent=$leftExtent, rightExtent=$rightExtent, terminal=$terminal)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)             return true

        if (other !is TerminalSPPFNode) return false

        if (!super.equals(other))       return false

        if (terminal != other.terminal) return false

        return true
    }

    override val hashCode : Int = Objects.hash(leftExtent, rightExtent, terminal)
    override fun hashCode() = hashCode
}
