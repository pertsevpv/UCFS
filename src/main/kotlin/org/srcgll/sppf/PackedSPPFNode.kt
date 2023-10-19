package org.srcgll.sppf

import org.srcgll.grammar.RSMState
import java.util.*

open class PackedSPPFNode
(
    val pivot         : Int, //  left extent of the right child
    val rsmState      : RSMState,
    val leftSPPFNode  : SPPFNode? = null,
    val rightSPPFNode : SPPFNode? = null,
    override var Id   : Int = SPPFNodeId.getFirstFreeSPPFNodeId()
)
    : ISPPFNode
{
    // Backwards reference to parent SPPF Node
    override val parents : HashSet<ISPPFNode> = HashSet()
    // TODO: Resolve integer overflow when both subtrees have Int.MAX_VALUE
    override var weight  : Int = (leftSPPFNode?.weight ?: 0) + (rightSPPFNode?.weight ?: 0)
    
    override fun toString() =
        "PackedSPPFNode(pivot=$pivot, rsmState=$rsmState, leftSPPFNode=$leftSPPFNode, rightSPPFNode=$rightSPPFNode)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)                       return true

        if (other !is PackedSPPFNode)             return false

        if (pivot != other.pivot)                 return false

        if (rsmState != other.rsmState)           return false

        if (leftSPPFNode != other.leftSPPFNode)   return false

        if (rightSPPFNode != other.rightSPPFNode) return false

//        if (weight != other.weight)               return false

        return true
    }

    val hashCode : Int = Objects.hash(pivot, rsmState, leftSPPFNode, rightSPPFNode)
    override fun hashCode() = hashCode
}
