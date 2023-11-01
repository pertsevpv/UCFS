package org.srcgll.input

class LinearInput<VertexType, LabelType> : InputGraph<VertexType, LabelType>()
{
    override val vertices : MutableMap<VertexType, VertexType> = HashMap()
    override val edges    : MutableMap<VertexType, MutableList<Edge<LabelType, VertexType>>> = HashMap()

    override var startVertex : VertexType? = null
    override var finalVertex : VertexType? = null

    override fun getVertex(vertex : VertexType?) : VertexType?
    {
        if (vertex == null)
            return startVertex

        return vertices.getOrDefault(vertex, null)
    }

    override fun addVertex(vertex : VertexType)
    {
        vertices[vertex] = vertex
    }

    override fun removeVertex(vertex : VertexType)
    {
        vertices.remove(vertex)
    }

    override fun getEdges(from : VertexType) : MutableList<Edge<LabelType, VertexType>>
    {
        return edges.getOrDefault(from, ArrayList())
    }
    override fun addEdge(from : VertexType, label : LabelType, to : VertexType) : Boolean
    {
        val edge = Edge(label, to)

        if (!edges.containsKey(from)) edges[from] = ArrayList()

        return edges.getValue(from).add(edge)
    }


    override fun removeEdge(from : VertexType, label : LabelType, to : VertexType) : Boolean
    {
        val edge = Edge(label, to)

        return edges.getValue(from).remove(edge)
    }

    override fun isStart(vertex : VertexType?) = (vertex == startVertex)
    override fun isFinal(vertex : VertexType?) = (vertex == finalVertex)
}