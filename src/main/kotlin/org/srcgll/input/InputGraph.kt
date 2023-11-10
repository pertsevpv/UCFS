package org.srcgll.input

interface IGraph <VertexType, TerminalType, LabelType : ILabel<TerminalType>>
{
    val vertices      : MutableMap<VertexType, VertexType>
    val edges         : MutableMap<VertexType, MutableList<Edge<VertexType, TerminalType, LabelType>>>
    val startVertices : MutableSet<VertexType>

    fun getInputStartVertices() : MutableSet<VertexType>
    fun getVertex(vertex : VertexType?) : VertexType?
    fun addStartVertex(vertex : VertexType)
    fun addVertex(vertex : VertexType)
    fun removeVertex(vertex : VertexType)
    fun getEdges(from : VertexType) : MutableList<Edge<VertexType, TerminalType, LabelType>>
    fun addEdge(from : VertexType, label : LabelType, to : VertexType)
    fun removeEdge(from : VertexType, label : LabelType, to : VertexType)
    fun isStart(vertex : VertexType) : Boolean
    fun isFinal(vertex : VertexType) : Boolean
}