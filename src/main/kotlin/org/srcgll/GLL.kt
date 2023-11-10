package org.srcgll

import org.srcgll.grammar.RSMState
import org.srcgll.grammar.RSMTerminalEdge
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.descriptors.*
import org.srcgll.grammar.RSMNonterminalEdge
import org.srcgll.grammar.symbol.Terminal
import org.srcgll.gss.*
import org.srcgll.input.ILabel
import org.srcgll.input.IGraph
import org.srcgll.sppf.node.*
import org.srcgll.sppf.*

class GLL <VertexType, TerminalType, LabelType : ILabel<TerminalType>>
(
    private val startState : RSMState<TerminalType>,
    private val input      : IGraph<VertexType, TerminalType, LabelType>,
    private val recovery   : RecoveryMode,
)
{
    private val stack             : IDescriptorsStack<VertexType, TerminalType> = ErrorRecoveringDescriptorsStack()
    private val sppf              : SPPF<VertexType> = SPPF()
    private val poppedGSSNodes    : HashMap<GSSNode<VertexType, TerminalType>, HashSet<SPPFNode<VertexType>?>> = HashMap()
    private val createdGSSNodes   : HashMap<GSSNode<VertexType, TerminalType>, GSSNode<VertexType, TerminalType>> = HashMap()
    private var parseResult       : SPPFNode<VertexType>? = null
    private val reachableVertices : HashSet<VertexType> = HashSet()

    fun parse() : Pair<SPPFNode<VertexType>?, HashSet<VertexType>>
    {
        for (startVertex in input.getInputStartVertices()) {
            val descriptor =
                    Descriptor(
                        startState,
                        getOrCreateGSSNode(startState.nonterminal, startVertex, weight = 0),
                        sppfNode = null,
                        startVertex
                    )
            stack.add(descriptor)
        }

        // Continue parsing until all default descriptors processed
        while (stack.defaultDescriptorsStackIsNotEmpty()) {
            val curDefaultDescriptor = stack.next()

            parse(curDefaultDescriptor)
        }

        // If string was not parsed - process recovery descriptors until first valid parse tree is found
        // Due to the Error Recovery algorithm used it will be parse tree of the string with min editing cost
        while (recovery == RecoveryMode.ON && parseResult == null) {
            val curRecoveryDescriptor = stack.next()

            parse(curRecoveryDescriptor)
        }

        return Pair(parseResult, reachableVertices)
    }

    private fun parse(curDescriptor : Descriptor<VertexType, TerminalType>)
    {
        val state       = curDescriptor.rsmState
        val pos         = curDescriptor.inputPosition
        val gssNode     = curDescriptor.gssNode
        var curSPPFNode = curDescriptor.sppfNode
        var leftExtent  = curSPPFNode?.leftExtent
        var rightExtent = curSPPFNode?.rightExtent

        stack.addToHandled(curDescriptor)

        if (state.isStart && state.isFinal) {
            curSPPFNode = sppf.getNodeP(
                              state,
                              curSPPFNode,
                              sppf.getOrCreateItemSPPFNode(
                                  state,
                                  pos,
                                  pos,
                                  weight = 0
                                  )
                              )
            leftExtent = curSPPFNode.leftExtent
            rightExtent = curSPPFNode.rightExtent
        }

        if (curSPPFNode is SymbolSPPFNode<VertexType>
            && state.nonterminal == startState.nonterminal && input.isStart(leftExtent!!) && input.isFinal(rightExtent!!)) {

            if (parseResult == null || parseResult!!.weight > curSPPFNode.weight) {
                parseResult = curSPPFNode
            }
            reachableVertices.add(rightExtent)
        }

        for (inputEdge in input.getEdges(pos)) {
            for (kvp in state.outgoingTerminalEdges) {
                if (inputEdge.label.terminal == kvp.key) {
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
                                            inputEdge.head,
                                            weight = 0
                                        )
                                    ),
                                    inputEdge.head
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
                            sppfNode = null,
                            pos
                        )
                stack.add(descriptor)
            }
        }

        if (recovery == RecoveryMode.ON) {
            val errorRecoveryEdges = HashMap<Terminal<TerminalType>?, TerminalRecoveryEdge<VertexType>>()
            val currentEdges       = input.getEdges(pos)

            if (currentEdges.isNotEmpty()) {
                for (currentEdge in currentEdges) {
                    val currentTerminal = currentEdge.label.terminal

                    val coveredByCurrentTerminal : HashSet<RSMState<TerminalType>> =
                        if (state.outgoingTerminalEdges.containsKey(currentTerminal)) {
                            state.outgoingTerminalEdges.getValue(currentTerminal)
                        } else {
                            HashSet()
                        }
                    for (terminal in state.errorRecoveryLabels) {
                        val coveredByTerminal = HashSet(state.outgoingTerminalEdges[terminal] as HashSet<RSMState<TerminalType>>)

                        coveredByCurrentTerminal.forEach { coveredByTerminal.remove(it) }

                        if (terminal != currentTerminal && coveredByTerminal.isNotEmpty()) {
                            errorRecoveryEdges[terminal] = TerminalRecoveryEdge(pos, weight = 1)
                        }
                    }
                    errorRecoveryEdges[null] = TerminalRecoveryEdge(currentEdge.head, weight = 1)
                }
            } else {
                for (terminal in state.errorRecoveryLabels) {
                    val coveredByTerminal = HashSet(state.outgoingTerminalEdges[terminal] as HashSet<RSMState<TerminalType>>)

                    if (coveredByTerminal.isNotEmpty()) {
                        errorRecoveryEdges[terminal] = TerminalRecoveryEdge(pos, weight = 1)
                    }
                }
            }

            for (kvp in errorRecoveryEdges) {
                val errorRecoveryEdge = kvp.value
                val terminal          = kvp.key

                if (terminal == null) {
                    handleTerminalOrEpsilonEdge(curDescriptor, terminal = null, errorRecoveryEdge, curDescriptor.rsmState)
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

    private fun handleTerminalOrEpsilonEdge
    (
        curDescriptor : Descriptor<VertexType, TerminalType>,
        terminal      : Terminal<TerminalType>?,
        targetEdge    : TerminalRecoveryEdge<VertexType>,
        targetState   : RSMState<TerminalType>
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

    private fun getOrCreateGSSNode(nonterminal : Nonterminal<TerminalType>, inputPosition : VertexType, weight : Int)
        : GSSNode<VertexType, TerminalType>
    {
        val gssNode = GSSNode(nonterminal, inputPosition, weight)

        if (createdGSSNodes.containsKey(gssNode)) {
            if (createdGSSNodes.getValue(gssNode).minWeightOfLeftPart > weight)
                createdGSSNodes.getValue(gssNode).minWeightOfLeftPart = weight
        } else
            createdGSSNodes[gssNode] = gssNode

        return createdGSSNodes.getValue(gssNode)
    }


    private fun createGSSNode
    (
        nonterminal  : Nonterminal<TerminalType>,
        state        : RSMState<TerminalType>,
        gssNode      : GSSNode<VertexType, TerminalType>,
        sppfNode     : SPPFNode<VertexType>?,
        pos          : VertexType,
    )
        : GSSNode<VertexType, TerminalType>
    {
        val newNode= getOrCreateGSSNode(nonterminal, pos, weight = gssNode.minWeightOfLeftPart + (sppfNode?.weight ?: 0))

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

    private fun pop(gssNode : GSSNode<VertexType, TerminalType>, sppfNode : SPPFNode<VertexType>?, pos : VertexType)
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
