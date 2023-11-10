package org.srcgll.input

class LinearInput<VertexType, TerminalType, LabelType : ILabel<TerminalType>>
    : IGraph<VertexType, TerminalType, LabelType>
{
    override val vertices : MutableMap<VertexType, VertexType> = HashMap()
    override val edges    : MutableMap<VertexType, MutableList<Edge<VertexType, TerminalType, LabelType>>> = HashMap()

    override val startVertices : MutableSet<VertexType> = HashSet()

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

    override fun getEdges(from : VertexType) : MutableList<Edge<VertexType, TerminalType, LabelType>>
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

    override fun isStart(vertex : VertexType) = startVertices.contains(vertex)
    override fun isFinal(vertex : VertexType) = getEdges(vertex).isEmpty()
}