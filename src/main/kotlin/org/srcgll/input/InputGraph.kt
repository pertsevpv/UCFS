package org.srcgll.input

abstract class InputGraph <VertexType, LabelType : ILabel>
{
    abstract val vertices : MutableMap<VertexType, VertexType>
    abstract val edges    : MutableMap<VertexType, MutableList<Edge<LabelType, VertexType>>>

    abstract val startVertices : MutableSet<VertexType>

    abstract var finalVertex : VertexType?

    abstract fun getInputStartVertices() : MutableSet<VertexType>

    abstract fun getVertex(vertex : VertexType?) : VertexType?

    abstract fun addStartVertex(vertex : VertexType)

    abstract fun addVertex(vertex : VertexType)

    abstract fun removeVertex(vertex : VertexType)

    abstract fun getEdges(from : VertexType) : MutableList<Edge<LabelType, VertexType>>

    abstract fun addEdge(from : VertexType, label : LabelType, to : VertexType)


    abstract fun removeEdge(from : VertexType, label : LabelType, to : VertexType)

    abstract fun isStart(vertex : VertexType?) : Boolean
    abstract fun isFinal(vertex : VertexType?) : Boolean
}