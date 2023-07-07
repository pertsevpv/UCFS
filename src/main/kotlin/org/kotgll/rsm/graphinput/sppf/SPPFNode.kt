package org.kotgll.rsm.graphinput.sppf

import org.kotgll.graph.GraphNode
import org.kotgll.rsm.grammar.symbol.Symbol
import java.util.*

open class SPPFNode(val leftExtent : GraphNode, val rightExtent : GraphNode)
{
    override fun toString() = "SPPFNode(leftExtent=$leftExtent, rightExtent=$rightExtent)"

    override fun equals(other : Any?) : Boolean
    {
        if (other !is SPPFNode)               return false

        if (leftExtent != other.leftExtent)   return false

        return rightExtent == other.rightExtent
    }

    open val hashCode : Int = Objects.hash(leftExtent, rightExtent)
    override fun hashCode() = hashCode

    open fun hasSymbol(symbol : Symbol) = false
}
