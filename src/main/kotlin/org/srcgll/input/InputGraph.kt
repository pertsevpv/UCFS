package org.srcgll.input

abstract class InputGraph <VertexType,LabelType>
{
    abstract val vertices : MutableMap<VertexType, VertexType>
    abstract val edges    : MutableMap<VertexType, MutableList<Edge<LabelType, VertexType>>>

    abstract var startVertex : VertexType?
    abstract var finalVertex : VertexType?

    abstract fun getVertex(vertex : VertexType?) : VertexType?

    abstract fun addVertex(vertex : VertexType)

    abstract fun removeVertex(vertex : VertexType)

    abstract fun getEdges(from : VertexType) : MutableList<Edge<LabelType, VertexType>>

    abstract fun addEdge(from : VertexType, label : LabelType, to : VertexType) : Boolean


    abstract fun removeEdge(from : VertexType, label : LabelType, to : VertexType) : Boolean

    abstract fun isStart(vertex : VertexType?) : Boolean
    abstract fun isFinal(vertex : VertexType?) : Boolean
}