package org.kotgll.rsm.graphinput

import org.kotgll.graph.GraphNode
import org.kotgll.rsm.grammar.RSMState
import org.kotgll.rsm.graphinput.sppf.SPPFNode
import java.util.*
import kotlin.collections.ArrayDeque

class DescriptorsQueue
{
    val todo    : ArrayDeque<Descriptor>                  = ArrayDeque()
    val created : HashMap<GraphNode, HashSet<Descriptor>> = HashMap()

    fun add(rsmState : RSMState, gssNode : GSSNode, sppfNode : SPPFNode?, pos : GraphNode)
    {
        val descriptor = Descriptor(rsmState, gssNode, sppfNode, pos)

        if (!created.containsKey(pos)) created[pos] = HashSet()

        if (created[pos]!!.add(descriptor)) todo.addLast(descriptor)
    }

    fun next() = todo.removeFirst()

    fun isEmpty() = todo.isEmpty()

    class Descriptor
    (
        val rsmState : RSMState,
        val gssNode  : GSSNode,
        val sppfNode : SPPFNode?,
        val pos      : GraphNode,
    )
    {
        override fun toString() =
            "Descriptor(rsmState=$rsmState, gssNode=$gssNode, sppfNode=$sppfNode, pos=$pos)"

        override fun equals(other : Any?) : Boolean
        {
            if (other !is Descriptor)       return false

            if (rsmState != other.rsmState) return false

            if (gssNode != other.gssNode)   return false

            return sppfNode != other.sppfNode
        }

        val hashCode: Int = Objects.hash(rsmState, gssNode, sppfNode)
        override fun hashCode() = hashCode
    }
}
