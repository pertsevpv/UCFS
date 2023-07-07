package org.kotgll.graph

class GraphNode(val id : Int, var isStart : Boolean = false, var isFinal : Boolean = false)
{
    var outgoingEdges : HashMap<String, ArrayList<GraphNode>> = HashMap()

    override fun toString() = "GraphNode(id=$id, isStart=$isStart, isFinal=$isFinal)"

    override fun equals(other : Any?) : Boolean
    {
        if (other !is GraphNode) return false

        if (this === other)      return true

        return id == other.id
    }

    val hashCode : Int = id
    override fun hashCode() = hashCode

    fun addEdge(label : String, head : GraphNode)
    {
        if (!outgoingEdges.containsKey(label)) outgoingEdges[label] = ArrayList()
        outgoingEdges[label]!!.add(head)
    }
}
