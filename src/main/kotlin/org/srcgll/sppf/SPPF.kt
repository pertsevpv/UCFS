package org.srcgll.sppf

import org.srcgll.grammar.RSMState
import org.srcgll.grammar.symbol.ITerminal
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Terminal
import java.io.File

class SPPF <VertexType>
{
    private val createdSPPFNodes : HashMap<SPPFNode<VertexType>, SPPFNode<VertexType>> = HashMap()

    fun getNodeP(state : RSMState, sppfNode : SPPFNode<VertexType>?, nextSPPFNode : SPPFNode<VertexType>) : SPPFNode<VertexType>
    {
        val leftExtent  = sppfNode?.leftExtent ?: nextSPPFNode.leftExtent
        val rightExtent = nextSPPFNode.rightExtent

        val packedNode = PackedSPPFNode(nextSPPFNode.leftExtent, state, sppfNode, nextSPPFNode)

        val parent : ParentSPPFNode<VertexType> =
            if (state.isFinal) getOrCreateSymbolSPPFNode(state.nonterminal, leftExtent, rightExtent, packedNode.weight)
            else               getOrCreateItemSPPFNode(state, leftExtent, rightExtent, packedNode.weight)


        sppfNode?.parents?.add(packedNode)
        nextSPPFNode.parents.add(packedNode)
        packedNode.parents.add(parent)

        parent.kids.add(packedNode)

        parent.updateWeights()

        return parent
    }

    fun getOrCreateTerminalSPPFNode
                (
        terminal    : ITerminal?,
        leftExtent  : VertexType,
        rightExtent : VertexType,
        weight      : Int
    )
            : SPPFNode<VertexType>
    {
        val node = TerminalSPPFNode(terminal, leftExtent, rightExtent, weight)

        if (!createdSPPFNodes.containsKey(node)) {
            createdSPPFNodes[node] = node
        }

        return createdSPPFNodes[node]!!
    }

    fun getOrCreateItemSPPFNode
                (
        state       : RSMState,
        leftExtent  : VertexType,
        rightExtent : VertexType,
        weight      : Int
    )
            : ParentSPPFNode<VertexType>
    {
        val node = ItemSPPFNode(state, leftExtent, rightExtent)
        node.weight = weight

        if (!createdSPPFNodes.containsKey(node)) {
            createdSPPFNodes[node] = node
        }

        return createdSPPFNodes[node]!! as ItemSPPFNode
    }

    fun getOrCreateSymbolSPPFNode
                (
        nonterminal : Nonterminal,
        leftExtent  : VertexType,
        rightExtent : VertexType,
        weight      : Int
    )
            : SymbolSPPFNode<VertexType>
    {
        val node = SymbolSPPFNode(nonterminal, leftExtent, rightExtent)
        node.weight = weight

        if (!createdSPPFNodes.containsKey(node)) createdSPPFNodes[node] = node


        return createdSPPFNodes[node]!! as SymbolSPPFNode
    }

    companion object {

        fun toDot(sppfNode : ISPPFNode, filePath : String)
        {
            val queue : ArrayDeque<ISPPFNode> = ArrayDeque(listOf(sppfNode))
            val edges : HashMap<Int, HashSet<Int>> = HashMap()
            val visited : HashSet<Int> = HashSet()
            var node : ISPPFNode
            val file = File(filePath)

            file.printWriter().use {
                    out ->
                out.println("digraph g {")

                while (queue.isNotEmpty()) {
                    node = queue.removeFirst()
                    if (!visited.add(node.id)) continue

                    out.println(printNode(node.id, node))

                    (node as? ParentSPPFNode<*>)?.kids?.forEach {
                        queue.addLast(it)
                        if (!edges.containsKey(node.id)) {
                            edges[node.id] = HashSet()
                        }
                        edges.getValue(node.id).add(it.id)
                    }

                    val leftChild  = (node as? PackedSPPFNode<*>)?.leftSPPFNode
                    val rightChild = (node as? PackedSPPFNode<*>)?.rightSPPFNode

                    if (leftChild != null) {
                        queue.addLast(leftChild)
                        if (!edges.containsKey(node.id)) {
                            edges[node.id] = HashSet()
                        }
                        edges.getValue(node.id).add(leftChild.id)
                    }
                    if (rightChild != null) {
                        queue.addLast(rightChild)
                        if (!edges.containsKey(node.id)) {
                            edges[node.id] = HashSet()
                        }
                        edges.getValue(node.id).add(rightChild.id)
                    }
                }
                for (kvp in edges) {
                    val head = kvp.key
                    for (tail in kvp.value)
                        out.println(printEdge(head, tail))
                }
                out.println("}")
            }
        }
        private fun getColor(weight : Int) : String = if (weight == 0) "black" else "red"

        private fun printEdge(x : Int, y : Int) : String
        {
            return "${x}->${y}"
        }

        private fun printNode(nodeId : Int, node : ISPPFNode) : String
        {
            return when(node) {
                is TerminalSPPFNode<*> -> {
                    "${nodeId} [label = \"${nodeId} ; ${node.terminal ?: "eps"}, ${node.leftExtent}, ${node.rightExtent}, Weight: ${node.weight}\", shape = ellipse, color = ${getColor(node.weight)}]"
                }
                is SymbolSPPFNode<*> -> {
                    "${nodeId} [label = \"${nodeId} ; ${node.symbol.value}, ${node.leftExtent}, ${node.rightExtent}, Weight: ${node.weight}\", shape = octagon, color = ${getColor(node.weight)}]"
                }
                is ItemSPPFNode<*> -> {
                    "${nodeId} [label = \"${nodeId} ; RSM: ${node.rsmState.nonterminal.value}, ${node.leftExtent}, ${node.rightExtent}, Weight: ${node.weight}\", shape = rectangle, color = ${getColor(node.weight)}]"
                }
                is PackedSPPFNode<*> -> {
                    "${nodeId} [label = \"${nodeId} ; Weight: ${node.weight}\", shape = point, width = 0.5, color = ${getColor(node.weight)}]"
                }
                else -> ""
            }
        }
    }
}

