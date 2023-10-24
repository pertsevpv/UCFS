package org.srcgll.input

class Graph <VertexType, LabelType>
{
    private val vertices : HashMap<VertexType, VertexType> = HashMap()
    private val edges    : HashMap<VertexType, ArrayList<Pair<LabelType, VertexType>>> = HashMap()

    var startVertex : VertexType? = null
    var finalVertex : VertexType? = null

    fun getVertex(vertex : VertexType? = null) : VertexType?
    {
        if (vertex == null)
            return startVertex

        return vertices.getOrDefault(vertex, null)
    }

    fun addVertex(vertex : VertexType)
    {
        vertices[vertex] = vertex
    }

    fun removeVertex(vertex : VertexType)
    {
        vertices.remove(vertex)
    }

    fun getEdges(from : VertexType) : ArrayList<Pair<LabelType, VertexType>>
    {
        if (edges.containsKey(from))
            return edges.getValue(from)

        return ArrayList()
    }
    fun addEdge(from : VertexType, label : LabelType, to : VertexType) : Boolean
    {
        val edge = Pair(label, to)

        if (!edges.containsKey(from)) edges[from] = ArrayList()

        return edges.getValue(from).add(edge)
    }


    fun removeEdge(from : VertexType, label : LabelType, to : VertexType) : Boolean
    {
        val edge = Pair(label, to)

        return edges.getValue(from).remove(edge)
    }

    fun isStart(vertex : VertexType?) = (vertex == startVertex)
    fun isFinal(vertex : VertexType?) = (vertex == finalVertex)
}