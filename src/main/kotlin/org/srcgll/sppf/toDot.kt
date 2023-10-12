package org.srcgll.sppf

import java.io.File

fun getColor(weight : Int) : String = if (weight == 0) "black" else "red"

fun printEdge(x : Int, y : Int) : String
{
    return "${x}->${y}"
}

fun printNode(nodeId : Int, node : ISPPFNode) : String
{
    return when(node) {
        is TerminalSPPFNode -> {
            "${nodeId} [label = \"${nodeId} ; ${node.terminal ?: "eps"}, ${node.leftExtent}, ${node.rightExtent}, Weight: ${node.weight}\", shape = ellipse, color = ${getColor(node.weight)}]"
        }
        is SymbolSPPFNode -> {
            "${nodeId} [label = \"${nodeId} ; ${node.symbol.value}, ${node.leftExtent}, ${node.rightExtent}, Weight: ${node.weight}\", shape = octagon, color = ${getColor(node.weight)}]"
        }
        is ItemSPPFNode -> {
            "${nodeId} [label = \"${nodeId} ; RSM: ${node.rsmState.nonterminal.value}, ${node.leftExtent}, ${node.rightExtent}, Weight: ${node.weight}\", shape = rectangle, color = ${getColor(node.weight)}]"
        }
        is PackedSPPFNode -> {
            "${nodeId} [label = \"${nodeId} ; Weight: ${node.weight}\", shape = point, width = 0.5, color = ${getColor(node.weight)}]"
        }
        else -> ""
    }
}

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
                if (!visited.add(node.Id)) continue

                out.println(printNode(node.Id, node))

                (node as? ParentSPPFNode)?.kids?.forEach {
                    queue.addLast(it)
                    if (!edges.containsKey(node.Id)) {
                        edges[node.Id] = HashSet()
                    }
                    edges.getValue(node.Id).add(it.Id)
                }

                val leftChild  = (node as? PackedSPPFNode)?.leftSPPFNode
                val rightChild = (node as? PackedSPPFNode)?.rightSPPFNode

                if (leftChild != null) {
                    queue.addLast(leftChild)
                    if (!edges.containsKey(node.Id)) {
                        edges[node.Id] = HashSet()
                    }
                    edges.getValue(node.Id).add(leftChild.Id)
                }
                if (rightChild != null) {
                    queue.addLast(rightChild)
                    if (!edges.containsKey(node.Id)) {
                        edges[node.Id] = HashSet()
                    }
                    edges.getValue(node.Id).add(rightChild.Id)
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