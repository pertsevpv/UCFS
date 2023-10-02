package org.srcgll.descriptors

class TerminalEdgeTarget
(
    val targetPosition : Int,
    val weight         : Int = 0,
)

class GraphNode
(
    val id      : Int,
    var isStart : Boolean = false,
    var isFinal : Boolean = false,
)
{
    var outgoingEdges : HashMap<String, ArrayList<TerminalEdgeTarget>> = HashMap()

    override fun toString() = "GraphNode(id=$id, isStart=$isStart, isFinal=$isFinal)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)      return true

        if (other !is GraphNode) return false

        if (id != other.id)      return false

        return true
    }

    val hashCode : Int = id
    override fun hashCode() = hashCode

    fun addEdge(label : String, target : TerminalEdgeTarget)
    {
        if (!outgoingEdges.containsKey(label)) outgoingEdges[label] = ArrayList()
        
        outgoingEdges[label]!!.add(target)
    }
}
