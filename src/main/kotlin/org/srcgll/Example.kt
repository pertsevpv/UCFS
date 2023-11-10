package org.srcgll

import org.srcgll.grammar.combinator.Grammar
import org.srcgll.grammar.combinator.regexp.*
import org.srcgll.grammar.symbol.Terminal
import org.srcgll.input.Edge
import org.srcgll.input.ILabel
import org.srcgll.input.IGraph
import org.srcgll.sppf.node.SPPFNode

/**
 * Define Class for a^n b^n Language CF-Grammar
 * @param TerminalType = String
 */
class AnBn : Grammar()
{
    // Nonterminals
    var S by NT()

    init {
        // Production rules. 'or' is Alternative, '*' is Concatenation
         S = Epsilon or Term("a") * S * Term("b")

        // Set Starting Nonterminal
        setStart(S)
    }
}

/**
 * Realisation of ILabel interface which represents label on Input Graph edges
 * @param TerminalType = String
 */
class SimpleInputLabel
(
    label : String
)
    : ILabel
{
    override val terminal : Terminal<String> = Terminal(label)

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)             return true
        if (other !is SimpleInputLabel) return false
        if (terminal != other.terminal) return false
        return true
    }
}

/**
 * Simple Realisation of IGraph interface as Directed Graph
 * @param VertexType   = Int
 * @param TerminalType = String
 * @param LabelType    = SimpleInputLabel
 */
class SimpleGraph : IGraph<Int, SimpleInputLabel>
{
    override val vertices : MutableMap<Int, Int> = HashMap()
    override val edges    : MutableMap<Int, MutableList<Edge<Int, SimpleInputLabel>>> = HashMap()

    override val startVertices : MutableSet<Int> = HashSet()

    override fun getInputStartVertices() : MutableSet<Int>  = startVertices

    override fun isFinal(vertex : Int) : Boolean = true

    override fun isStart(vertex : Int) : Boolean = startVertices.contains(vertex)

    override fun removeEdge(from : Int, label : SimpleInputLabel, to : Int)
    {
        val edge = Edge(label, to)

        edges.getValue(from).remove(edge)
    }

    override fun addEdge(from : Int, label : SimpleInputLabel, to : Int)
    {
        val edge = Edge(label, to)
        if (!edges.containsKey(from)) edges[from] = ArrayList()
        edges.getValue(from).add(edge)
    }

    override fun getEdges(from : Int) : MutableList<Edge<Int, SimpleInputLabel>>
    {
        return edges.getOrDefault(from, ArrayList())
    }

    override fun removeVertex(vertex : Int)
    {
        vertices.remove(vertex)
    }

    override fun addVertex(vertex : Int)
    {
        vertices[vertex] = vertex
    }

    override fun addStartVertex(vertex : Int)
    {
        startVertices.add(vertex)
    }

    override fun getVertex(vertex : Int?) : Int?
    {
        return vertices.getOrDefault(vertex, null)
    }
}

fun main()
{
    val rsmStartState = AnBn().buildRsm()
    val inputGraph    = SimpleGraph()
    val startVertex   = 0

    for (i in 0..3) inputGraph.addVertex(vertex = i)

    inputGraph.addEdge(from = 0, to = 1, label = SimpleInputLabel("a"))
    inputGraph.addEdge(from = 1, to = 2, label = SimpleInputLabel("a"))
    inputGraph.addEdge(from = 2, to = 0, label = SimpleInputLabel("a"))
    inputGraph.addEdge(from = 0, to = 3, label = SimpleInputLabel("b"))
    inputGraph.addEdge(from = 3, to = 0, label = SimpleInputLabel("b"))

    // addStartVertex does not add Vertex to list of Vertices, so for starting vertices there should be both
    // calls to addVertex and addStartVertex
    inputGraph.addStartVertex(startVertex)

    // result = (root of SPPF, set of reachable vertices)
    val result : Pair<SPPFNode<Int>?, HashSet<Int>> = GLL<Int, SimpleInputLabel>(rsmStartState, inputGraph, recovery = RecoveryMode.OFF).parse()

    println("Reachable vertices from vertex $startVertex : ")
    for (reachable in result.second) {
        println("Vertex: $reachable")
    }
}