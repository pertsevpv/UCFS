package org.srcgll.input

class LinearInput<VertexType, LabelType : ILabel> : InputGraph<VertexType, LabelType>()
{
    override val vertices : MutableMap<VertexType, VertexType> = HashMap()
    override val edges    : MutableMap<VertexType, MutableList<Edge<LabelType, VertexType>>> = HashMap()

    override val startVertices : MutableSet<VertexType> = HashSet()

    override var finalVertex : VertexType? = null

    override fun getInputStartVertices() : MutableSet<VertexType>
    {
        return startVertices
    }

    override fun getVertex(vertex : VertexType?) : VertexType?
    {
        return vertices.getOrDefault(vertex, null)
    }

    override fun addStartVertex(vertex : VertexType)
    {
        startVertices.add(vertex)
        vertices[vertex] = vertex
    }

    override fun addVertex(vertex : VertexType)
    {
        vertices[vertex] = vertex
    }

    override fun removeVertex(vertex : VertexType)
    {
        startVertices.remove(vertex)
        vertices.remove(vertex)
    }

    override fun getEdges(from : VertexType) : MutableList<Edge<LabelType, VertexType>>
    {
        return edges.getOrDefault(from, ArrayList())
    }
    override fun addEdge(from : VertexType, label : LabelType, to : VertexType)
    {
        val edge = Edge(label, to)

        if (!edges.containsKey(from)) edges[from] = ArrayList()

        edges.getValue(from).add(edge)
    }


    override fun removeEdge(from : VertexType, label : LabelType, to : VertexType)
    {
        val edge = Edge(label, to)
        edges.getValue(from).remove(edge)
    }

    override fun isStart(vertex : VertexType?) = startVertices.contains(vertex)
    override fun isFinal(vertex : VertexType?) = (vertex == finalVertex)
}