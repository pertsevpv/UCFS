package org.kotgll.rsm.graphinput.sppf

import org.kotgll.graph.GraphNode
import org.kotgll.rsm.grammar.RSMState
import java.util.*

open class PackedSPPFNode
(
    val pivot         : GraphNode,
    val rsmState      : RSMState,
    val leftSPPFNode  : SPPFNode? = null,
    val rightSPPFNode : SPPFNode? = null,
)
{
    override fun toString() =
        "PackedSPPFNode(pivot=$pivot, rsmState=$rsmState, leftSPPFNode=$leftSPPFNode, rightSPPFNode=$rightSPPFNode)"

    override fun equals(other : Any?) : Boolean
    {
        if (other !is PackedSPPFNode)             return false

        if (rightSPPFNode != other.rightSPPFNode) return false

        if (leftSPPFNode != other.leftSPPFNode)   return false

        if (pivot != other.pivot)                 return false

        if (this === other)                       return true

        return rsmState == other.rsmState
    }

    val hashCode: Int = Objects.hash(pivot, rsmState, leftSPPFNode, rightSPPFNode)
    override fun hashCode() = hashCode
}
