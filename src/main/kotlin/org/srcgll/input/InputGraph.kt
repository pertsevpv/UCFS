package org.srcgll.input

abstract class InputGraph <VertexType, TerminalType, LabelType : ILabel<TerminalType>>
{
    abstract val vertices : MutableMap<VertexType, VertexType>
    abstract val edges    : MutableMap<VertexType, MutableList<Edge<VertexType, TerminalType, LabelType>>>

    abstract val startVertices : MutableSet<VertexType>

    abstract fun getInputStartVertices() : MutableSet<VertexType>

    abstract fun getVertex(vertex : VertexType?) : VertexType?

    abstract fun addStartVertex(vertex : VertexType)

    abstract fun addVertex(vertex : VertexType)

    abstract fun removeVertex(vertex : VertexType)

    abstract fun getEdges(from : VertexType) : MutableList<Edge<VertexType, TerminalType, LabelType>>

    abstract fun addEdge(from : VertexType, label : LabelType, to : VertexType)

    abstract fun removeEdge(from : VertexType, label : LabelType, to : VertexType)

    abstract fun isStart(vertex : VertexType) : Boolean
    abstract fun isFinal(vertex : VertexType) : Boolean
}