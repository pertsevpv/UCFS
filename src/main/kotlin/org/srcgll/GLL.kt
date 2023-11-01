package org.srcgll

import org.srcgll.grammar.RSMState
import org.srcgll.grammar.RSMTerminalEdge
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Terminal
import org.srcgll.descriptors.*
import org.srcgll.grammar.RSMNonterminalEdge
import org.srcgll.grammar.symbol.ITerminal
import org.srcgll.gss.*
import org.srcgll.input.InputGraph
import org.srcgll.sppf.*

class GLL <VertexType, LabelType : ITerminal>
(
    val startState : RSMState,
    val input      : InputGraph<VertexType, LabelType>,
    val recovery   : Boolean,
)
{

    val stack            : IDescriptorsStack<VertexType>                                = ErrorRecoveringDescriptorsStack()
    val sppf             : SPPF<VertexType>                                             = SPPF()
    val poppedGSSNodes   : HashMap<GSSNode<VertexType>, HashSet<SPPFNode<VertexType>?>> = HashMap()
    val createdGSSNodes  : HashMap<GSSNode<VertexType>, GSSNode<VertexType>>            = HashMap()
    var parseResult      : SPPFNode<VertexType>?                                        = null

    fun parse() : SPPFNode<VertexType>?
    {
        val descriptor =
                Descriptor(
                    startState,
                    getOrCreateGSSNode(startState.nonterminal, input.getVertex(null)!!, 0),
                    null,
                    input.getVertex(null)!!
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
            curSPPFNode = sppf.getNodeP(
                              state,
                              curSPPFNode,
                              sppf.getOrCreateItemSPPFNode(
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
                if (edge.label.match(pos as Any, kvp.key.value)) {
                    for (target in kvp.value) {
                        val rsmEdge = RSMTerminalEdge(kvp.key, target)

                        val descriptor =
                                Descriptor(
                                    rsmEdge.head,
                                    gssNode,
                                    sppf.getNodeP(
                                        rsmEdge.head,
                                        curSPPFNode,
                                        sppf.getOrCreateTerminalSPPFNode(
                                            rsmEdge.terminal,
                                            pos,
                                            edge.head,
                                            0
                                        )
                                    ),
                                    edge.head
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
            val errorRecoveryEdges = HashMap<Terminal?, TerminalRecoveryEdge<VertexType>>()

            for (currentEdge in input.getEdges(pos)) {
                val currentTerminal = Terminal(currentEdge.label.value)

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
                        errorRecoveryEdges[terminal] = TerminalRecoveryEdge(pos, 1)
                    }
                }
                errorRecoveryEdges[null] = TerminalRecoveryEdge(currentEdge.head, 1)
            }

            for (kvp in errorRecoveryEdges) {
                val errorRecoveryEdge = kvp.value
                val terminal          = kvp.key

                if (terminal == null) {
                    handleTerminalOrEpsilonEdge(curDescriptor, null, errorRecoveryEdge, curDescriptor.rsmState)
                } else {
                    if (state.outgoingTerminalEdges.containsKey(terminal)) {
                        for (targetState in state.outgoingTerminalEdges.getValue(terminal)) {
                            handleTerminalOrEpsilonEdge(curDescriptor, terminal,  errorRecoveryEdge, targetState)
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
        targetEdge    : TerminalRecoveryEdge<VertexType>,
        targetState   : RSMState
    )
    {
        val descriptor =
                Descriptor(
                    targetState,
                    curDescriptor.gssNode,
                    sppf.getNodeP(
                        targetState,
                        curDescriptor.sppfNode,
                        sppf.getOrCreateTerminalSPPFNode(
                            terminal,
                            curDescriptor.inputPosition,
                            targetEdge.head,
                            targetEdge.weight
                        )
                    ),
                    targetEdge.head
                )
        stack.add(descriptor)
    }

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
                                sppf.getNodeP(state, sppfNode, popped!!),
                                popped.rightExtent
                            )
                    stack.add(descriptor)
                }
            }
        }

        return newNode
    }

    fun pop(gssNode : GSSNode<VertexType>, sppfNode : SPPFNode<VertexType>?, pos : VertexType)
    {
        if (!poppedGSSNodes.containsKey(gssNode)) poppedGSSNodes[gssNode] = HashSet()

        poppedGSSNodes.getValue(gssNode).add(sppfNode)

        for (edge in gssNode.edges) {
            for (node in edge.value) {
                val descriptor =
                        Descriptor(
                            edge.key.first,
                            node,
                            sppf.getNodeP(edge.key.first, edge.key.second, sppfNode!!),
                            pos
                        )
                stack.add(descriptor)
            }
        }
    }
}
