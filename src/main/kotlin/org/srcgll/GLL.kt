package org.srcgll

import org.srcgll.grammar.RSMState
import org.srcgll.grammar.RSMTerminalEdge
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Terminal
import org.srcgll.descriptors.*
import org.srcgll.grammar.RSMNonterminalEdge
import org.srcgll.gss.*
import org.srcgll.input.Graph
import org.srcgll.lexer.SymbolCode
import org.srcgll.lexer.Token
import org.srcgll.sppf.*

class GLL <VertexType>
(
    val startState : RSMState,
    val input      : Graph<VertexType, Token<SymbolCode>>,
    val recovery   : Boolean,
)
{

    val stack            : IDescriptorsStack<VertexType>                                = ErrorRecoveringDescriptorsStack()
    val poppedGSSNodes   : HashMap<GSSNode<VertexType>, HashSet<SPPFNode<VertexType>?>> = HashMap()
    val createdGSSNodes  : HashMap<GSSNode<VertexType>, GSSNode<VertexType>>            = HashMap()
    val createdSPPFNodes : HashMap<SPPFNode<VertexType>, SPPFNode<VertexType>>          = HashMap()
    var parseResult      : SPPFNode<VertexType>? = null
    
    fun getOrCreateGSSNode(nonterminal : Nonterminal, inputPosition : VertexType, weight : Int) : GSSNode<VertexType>
    {
        val gssNode = GSSNode(nonterminal, inputPosition, weight)
        
        if (createdGSSNodes.containsKey(gssNode)) {
            if (createdGSSNodes.getValue(gssNode).minWeightOfLeftPart > weight)
                createdGSSNodes.getValue(gssNode).minWeightOfLeftPart = weight
        } else
            createdGSSNodes[gssNode] = gssNode

        return createdGSSNodes.getValue(gssNode)
    }

    fun parse() : SPPFNode<VertexType>?
    {
        val descriptor =
                Descriptor(
                    startState,
                    getOrCreateGSSNode(startState.nonterminal, input.getVertex()!!, 0),
                    null,
                    input.getVertex()!!
                )
        stack.add(descriptor)


        // Continue parsing until all default descriptors processed
        while (stack.defaultDescriptorsStackIsNotEmpty()) {
            val curDefaultDescriptor = stack.next()
            parse(curDefaultDescriptor)
        }

        // If string was not parsed - process recovery descriptors until first valid parse tree is found
        // Due to the Error Recovery algorithm used it will be parse tree of the string with min editing cost
        while (recovery && parseResult == null) {
            val curRecoveryDescriptor = stack.next()

            parse(curRecoveryDescriptor)
        }

        return parseResult
    }

    fun parse(curDescriptor : Descriptor<VertexType>)
    {
        var curSPPFNode = curDescriptor.sppfNode
        val state       = curDescriptor.rsmState
        val pos         = curDescriptor.inputPosition
        val gssNode     = curDescriptor.gssNode
        val leftExtent  = curSPPFNode?.leftExtent
        val rightExtent = curSPPFNode?.rightExtent

        stack.addToHandled(curDescriptor)

        if (state.isStart && state.isFinal)
            curSPPFNode = getNodeP(
                              state,
                              curSPPFNode,
                              getOrCreateItemSPPFNode(
                                  state,
                                  pos,
                                  pos,
                                  0
                                  )
                              )

        if (curSPPFNode is SymbolSPPFNode<VertexType> && (parseResult == null || parseResult!!.weight > curSPPFNode.weight)
            && state.nonterminal == startState.nonterminal && input.isStart(leftExtent) && input.isFinal(rightExtent)) {
            parseResult = curSPPFNode
        }

        for (kvp in state.outgoingTerminalEdges) {
            for (edge in input.getEdges(pos)) {
                if (edge.first.value == kvp.key.value) {
                    for (target in kvp.value) {
                        val rsmEdge = RSMTerminalEdge(kvp.key, target)

                        val descriptor =
                                Descriptor(
                                    rsmEdge.head,
                                    gssNode,
                                    getNodeP(
                                        rsmEdge.head,
                                        curSPPFNode,
                                        getOrCreateTerminalSPPFNode(
                                            rsmEdge.terminal,
                                            pos,
                                            edge.second,
                                            0
                                        )
                                    ),
                                    edge.second
                                )
                        stack.add(descriptor)
                    }
                }
            }
        }

        for (kvp in state.outgoingNonterminalEdges) {
            for (target in kvp.value) {
                val rsmEdge = RSMNonterminalEdge(kvp.key, target)

                val descriptor =
                        Descriptor(
                            rsmEdge.nonterminal.startState,
                            createGSSNode(rsmEdge.nonterminal, rsmEdge.head, gssNode, curSPPFNode, pos),
                            null,
                            pos
                        )
                stack.add(descriptor)
            }
        }

        if (recovery) {
            val errorRecoveryEdges = HashMap<Terminal?, Pair<VertexType, Int>>()

            for (currentEdge in input.getEdges(pos)) {
                val currentTerminal = Terminal(currentEdge.first.value)

                val coveredByCurrentTerminal : HashSet<RSMState> =
                    if (state.outgoingTerminalEdges.containsKey(currentTerminal)) {
                        state.outgoingTerminalEdges.getValue(currentTerminal)
                    } else {
                        HashSet()
                    }
                for (terminal in state.errorRecoveryLabels) {
                    val coveredByTerminal = HashSet(state.outgoingTerminalEdges[terminal] as HashSet<RSMState>)

                    coveredByCurrentTerminal.forEach { coveredByTerminal.remove(it) }

                    if (terminal != currentTerminal && coveredByTerminal.isNotEmpty()) {
                        errorRecoveryEdges[terminal] = Pair(pos, 1)
                    }
                }
                errorRecoveryEdges[null] = Pair(currentEdge.second, 1)
            }

            for (kvp in errorRecoveryEdges) {
                if (kvp.key == null) {
                    handleTerminalOrEpsilonEdge(curDescriptor,kvp.key, kvp.value, curDescriptor.rsmState)
                } else {
                    if (state.outgoingTerminalEdges.containsKey(kvp.key)) {
                        for (targetState in state.outgoingTerminalEdges[kvp.key]!!) {
                            handleTerminalOrEpsilonEdge(curDescriptor, kvp.key,  kvp.value, targetState)
                        }
                    }
                }
            }
        }

        if (state.isFinal) pop(gssNode, curSPPFNode, pos)
    }

    fun handleTerminalOrEpsilonEdge
    (
        curDescriptor : Descriptor<VertexType>,
        terminal      : Terminal?,
        targetEdge    : Pair<VertexType, Int>,
        targetState   : RSMState
    )
    {
        val descriptor =
                Descriptor(
                    targetState,
                    curDescriptor.gssNode,
                    getNodeP(
                        targetState,
                        curDescriptor.sppfNode,
                        getOrCreateTerminalSPPFNode(
                            terminal,
                            curDescriptor.inputPosition,
                            targetEdge.first,
                            targetEdge.second
                        )
                    ),
                    targetEdge.first
                )
        stack.add(descriptor)
    }

    fun pop(gssNode : GSSNode<VertexType>, sppfNode : SPPFNode<VertexType>?, pos : VertexType)
    {
        if (!poppedGSSNodes.containsKey(gssNode)) poppedGSSNodes[gssNode] = HashSet()

        poppedGSSNodes[gssNode]!!.add(sppfNode)

        for (edge in gssNode.edges) {
            for (node in edge.value) {
                val descriptor =
                        Descriptor(
                            edge.key.first,
                            node,
                            getNodeP(edge.key.first, edge.key.second, sppfNode!!),
                            pos
                        )
                stack.add(descriptor)
            }
        }
    }

    fun createGSSNode
    (
        nonterminal  : Nonterminal,
        state        : RSMState,
        gssNode      : GSSNode<VertexType>,
        sppfNode     : SPPFNode<VertexType>?,
        pos          : VertexType,
    )
        : GSSNode<VertexType>
    {
        val newNode : GSSNode<VertexType> = getOrCreateGSSNode(nonterminal, pos, gssNode.minWeightOfLeftPart + (sppfNode?.weight ?: 0))

        if (newNode.addEdge(state, sppfNode, gssNode)) {
            if (poppedGSSNodes.containsKey(newNode)) {
                for (popped in poppedGSSNodes[newNode]!!) {
                    val descriptor =
                            Descriptor(
                                state,
                                gssNode,
                                getNodeP(state, sppfNode, popped!!),
                                popped.rightExtent
                            )
                    stack.add(descriptor)
                }
            }
        }

        return newNode
    }

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

//        toDot(parent,"./debug_sppf.dot")

        return parent
    }

    fun getOrCreateTerminalSPPFNode
    (
        terminal    : Terminal?,
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
