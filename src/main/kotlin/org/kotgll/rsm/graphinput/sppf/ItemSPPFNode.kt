package org.kotgll.rsm.graphinput.sppf

import org.kotgll.graph.GraphNode
import org.kotgll.rsm.grammar.RSMState
import java.util.*

class ItemSPPFNode
(
    leftExtent   : GraphNode,
    rightExtent  : GraphNode,
    val rsmState : RSMState,
)
    : ParentSPPFNode(leftExtent, rightExtent)
{
    override fun toString() =
        "ItemSPPFNode(leftExtent=$leftExtent, rightExtent=$rightExtent, rsmState=$rsmState)"

    override fun equals(other : Any?) : Boolean
    {
        if (other !is ItemSPPFNode) return false

        if (this === other)         return true

        if (!super.equals(other))   return false

        return rsmState == other.rsmState
    }

    override val hashCode : Int = Objects.hash(leftExtent, rightExtent, rsmState)
    override fun hashCode() = hashCode
}
