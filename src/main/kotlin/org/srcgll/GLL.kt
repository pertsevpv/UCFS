package org.srcgll

import org.srcgll.grammar.RSMState
import org.srcgll.grammar.RSMTerminalEdge
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Terminal
import org.srcgll.descriptors.*
import org.srcgll.grammar.RSMNonterminalEdge
import org.srcgll.grammar.TokenSequence
import org.srcgll.gss.*
import org.srcgll.sppf.*

class GLL
(
    val startState : RSMState,
    val input      : TokenSequence,
    val recovery   : Boolean = true,
    val debug      : Boolean = false,
)
{

    val stack            : IDescriptorsStack                    = ErrorRecoveringDescriptorsStack()
    val poppedGSSNodes   : HashMap<GSSNode, HashSet<SPPFNode?>> = HashMap()
    val createdGSSNodes  : HashMap<GSSNode, GSSNode>            = HashMap()
    val createdSPPFNodes : HashMap<SPPFNode, SPPFNode>          = HashMap()
    var parseResult      : SPPFNode?                            = null
    
    fun getOrCreateGSSNode(nonterminal : Nonterminal, inputPosition : TokenSequence, weight : Int) : GSSNode
    {
        val gssNode = GSSNode(nonterminal, inputPosition, weight)
        
        if (createdGSSNodes.containsKey(gssNode)) {
            if (createdGSSNodes.getValue(gssNode).minWeightOfLeftPart > weight) {
                createdGSSNodes.getValue(gssNode).minWeightOfLeftPart = weight
            }
        } else {
            createdGSSNodes[gssNode] = gssNode
        }

        return createdGSSNodes.getValue(gssNode)
    }

    fun parse() : SPPFNode?
    {
        val descriptor =
                Descriptor(
                    startState,
                    getOrCreateGSSNode(startState.nonterminal, input, 0),
                    null,
                    input
                )
        addDescriptor(descriptor)


        // Continue parsing until all default descriptors processed
        while (stack.defaultDescriptorsStackIsNotEmpty()) {
            val curDefaultDescriptor = stack.next()
            parse(curDefaultDescriptor)
        }

        // TODO: Check if there is a need to check emptiness of errorRecovery descriptors stack
        // If string was not parsed - process recovery descriptors until first valid parse tree is found
        // Due to the Error Recovery algorithm used it will be parse tree of the string with min editing cost
        while (recovery && parseResult == null) {
            val curRecoveryDescriptor = stack.next()
            parse(curRecoveryDescriptor)
        }

        return parseResult
    }

    fun parse(curDescriptor : Descriptor)
    {
        var curSPPFNode = curDescriptor.sppfNode
        val state       = curDescriptor.rsmState
        val pos         = curDescriptor.inputPosition
        val gssNode     = curDescriptor.gssNode
        val leftExtent  = curSPPFNode?.leftExtent
        val rightExtent = curSPPFNode?.rightExtent

        addDescriptorToHandled(curDescriptor)


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

        if (curSPPFNode is SymbolSPPFNode && (parseResult == null || parseResult!!.weight > curSPPFNode.weight)
            && state.nonterminal == startState.nonterminal && (leftExtent?.isStart == true) && (rightExtent?.isFinal == true)) {
            parseResult = curSPPFNode
        }

        for (kvp in state.outgoingTerminalEdges) {
            if (pos.nextToken.containsKey(kvp.key)) {
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
                                        pos.nextToken.getValue(rsmEdge.terminal),
                                        0
                                    )
                                ),
                                pos.nextToken.getValue(rsmEdge.terminal)
                            )
                    addDescriptor(descriptor)
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
                addDescriptor(descriptor)
            }
        }

        if (recovery) {
            // null represents Epsilon "Terminal"
            val errorRecoveryEdges = HashMap<Terminal?, TerminalEdgeTarget>()

            for (currentTerminal in pos.nextToken.keys) {
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
                        errorRecoveryEdges[terminal] = TerminalEdgeTarget(pos, 1)
                    }
                }
                errorRecoveryEdges[null] = TerminalEdgeTarget(pos.nextToken.getValue(currentTerminal), 1)
            }



            for (kvp in errorRecoveryEdges) {
                if (kvp.key == null) {
                    handleTerminalOrEpsilonEdge(curDescriptor, curSPPFNode, kvp.key, kvp.value, curDescriptor.rsmState)
                } else {
                    if (state.outgoingTerminalEdges.containsKey(kvp.key)) {
                        for (targetState in state.outgoingTerminalEdges[kvp.key]!!) {
                            handleTerminalOrEpsilonEdge(curDescriptor, curSPPFNode, kvp.key,  kvp.value, targetState)
                        }
                    }
                }
            }
        }

        if (state.isFinal) pop(gssNode, curSPPFNode, pos)
    }

    // TODO: Possible bug location, creates TerminalNode in SPPF for Epsilon Terminal
    fun handleTerminalOrEpsilonEdge
    (
        curDescriptor : Descriptor,
        sppfNode      : SPPFNode?,
        terminal      : Terminal?,
        targetEdge    : TerminalEdgeTarget,
        targetState   : RSMState
    )
    {
        val descriptor =
                Descriptor(
                    targetState,
                    curDescriptor.gssNode,
                    getNodeP(
                        targetState,
                        sppfNode,
                        getOrCreateTerminalSPPFNode(
                            terminal,
                            curDescriptor.inputPosition,
                            targetEdge.targetPosition,
                            targetEdge.weight
                        )
                    ),
                    targetEdge.targetPosition
                )
        addDescriptor(descriptor)
    }

    fun pop(gssNode : GSSNode, sppfNode : SPPFNode?, pos : TokenSequence)
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
                addDescriptor(descriptor)
            }
        }
    }

    fun createGSSNode
    (
        nonterminal  : Nonterminal,
        state        : RSMState,
        gssNode      : GSSNode,
        sppfNode     : SPPFNode?,
        pos          : TokenSequence,
    )
        : GSSNode
    {
        val newNode : GSSNode = getOrCreateGSSNode(nonterminal, pos, gssNode.minWeightOfLeftPart + (sppfNode?.weight ?: 0))

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
                    addDescriptor(descriptor)
                }
            }
        }

        return newNode
    }

    fun getNodeP(state : RSMState, sppfNode : SPPFNode?, nextSPPFNode : SPPFNode) : SPPFNode
    {
        val leftExtent  = sppfNode?.leftExtent ?: nextSPPFNode.leftExtent
        val rightExtent = nextSPPFNode.rightExtent

        val packedNode = PackedSPPFNode(nextSPPFNode.leftExtent, state, sppfNode, nextSPPFNode)

        val parent : ParentSPPFNode =
            if (state.isFinal) getOrCreateSymbolSPPFNode(state.nonterminal, leftExtent, rightExtent, packedNode.weight)
            else               getOrCreateItemSPPFNode(state, leftExtent, rightExtent, packedNode.weight)


        sppfNode?.parents?.add(packedNode)
        nextSPPFNode.parents.add(packedNode)
        packedNode.parents.add(parent)

        parent.kids.add(packedNode)

        // Define weight of parent node as minimum of kids' weights
        updateWeights(parent)

//        if (parent is SymbolSPPFNode && (parseResult == null || parseResult!!.weight > parent.weight) && state.nonterminal == startState.nonterminal && leftExtent == 0 && rightExtent == input.length) {
//            parseResult = parent
//        }

        if (debug) {
            toDot(parent, "./debug_sppf.dot")
        }

        return parent
    }

    fun getOrCreateTerminalSPPFNode
    (
        terminal    : Terminal?,
        leftExtent  : TokenSequence,
        rightExtent : TokenSequence,
        weight      : Int
    )
        : SPPFNode
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
        leftExtent  : TokenSequence,
        rightExtent : TokenSequence,
        weight      : Int
    )
        : ParentSPPFNode
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
        leftExtent  : TokenSequence,
        rightExtent : TokenSequence,
        weight      : Int
    )
        : SymbolSPPFNode
    {
        val node = SymbolSPPFNode(nonterminal, leftExtent, rightExtent)
        node.weight = weight

        if (!createdSPPFNodes.containsKey(node)) createdSPPFNodes[node] = node


        return createdSPPFNodes[node]!! as SymbolSPPFNode
    }
    
    fun addDescriptor(descriptor : Descriptor)
    {
        if (!isThisDescriptorAlreadyHandled(descriptor)) {
            stack.add(descriptor)
        }
    }
    
    fun isThisDescriptorAlreadyHandled(descriptor : Descriptor) : Boolean
    {
        val handledDescriptor = descriptor.gssNode.handledDescriptors.find { descriptor.hashCode() == it.hashCode() }
        
        return handledDescriptor != null && handledDescriptor.weight() <= descriptor.weight()
    }
    
    fun addDescriptorToHandled(descriptor : Descriptor)
    {
        descriptor.gssNode.handledDescriptors.add(descriptor)
    }

    fun updateWeights(sppfNode : ISPPFNode)
    {
        var curNode : ISPPFNode
        val cycle = HashSet<ISPPFNode>()
        val deque = ArrayDeque(listOf(sppfNode))

        while (deque.isNotEmpty()) {
            curNode = deque.last()

            when (curNode) {
                is ItemSPPFNode -> {
                    if (!cycle.contains(curNode)) {
                        val added = cycle.add(curNode)
                        assert(added)

                        val oldWeight = curNode.weight
                        var newWeight = Int.MAX_VALUE

                        curNode.kids.forEach { newWeight = minOf(newWeight, it.weight) }

                        if (oldWeight > newWeight) {
                            curNode.weight = newWeight

                            curNode.kids.forEach { if (it.weight > newWeight) it.parents.remove(curNode) }
                            curNode.kids.removeIf { it.weight > newWeight }

                            curNode.parents.forEach { deque.addLast(it) }
                        }
                        if (deque.last() == curNode) {
                            val removed = cycle.remove(curNode)
                            assert(removed)
                        }
                    }
                }
                is PackedSPPFNode -> {
                    val oldWeight = curNode.weight
                    val newWeight = (curNode.leftSPPFNode?.weight ?: 0) + (curNode.rightSPPFNode?.weight ?: 0)

                    if (oldWeight > newWeight) {
                        curNode.weight = newWeight

                        curNode.parents.forEach { deque.addLast(it) }
                    }
                }
                is SymbolSPPFNode -> {
                    val oldWeight = curNode.weight
                    var newWeight = Int.MAX_VALUE

                    curNode.kids.forEach { newWeight = minOf(newWeight, it.weight) }

                    if (oldWeight > newWeight) {
                        curNode.weight = newWeight

                        curNode.kids.forEach { if (it.weight > newWeight) it.parents.remove(curNode) }
                        curNode.kids.removeIf { it.weight > newWeight }

                        curNode.parents.forEach { deque.addLast(it) }
                    }
                }
                else -> {
                    throw  Error("Terminal node can not be parent")
                }
            }

            // Didn't Add any new parents -> hence need to remove from stack
            if (curNode == deque.last()) deque.removeLast()
        }
    }
}
