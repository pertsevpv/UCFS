package org.srcgll.sppf

import org.srcgll.grammar.RSMState
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Terminal
import org.srcgll.sppf.node.*
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
        terminal    : Terminal<*>?,
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
}

