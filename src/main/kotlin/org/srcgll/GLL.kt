package org.srcgll

import org.srcgll.grammar.RSMState
import org.srcgll.grammar.RSMTerminalEdge
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Terminal
import org.srcgll.graph.GraphNode
import org.srcgll.descriptors.*
import org.srcgll.graph.TerminalEdgeTarget
import org.srcgll.gss.*
import org.srcgll.sppf.*

class GLL
(
    val startState      : RSMState,
    val startGraphNodes : ArrayList<GraphNode>,
)
{
    constructor(
        startState      : RSMState,
        startGraphNodes : List<GraphNode>
    ) : this(startState, ArrayList(startGraphNodes))

    val stack            : IDescriptorsStack                    = ErrorRecoveringDescriptorsStack()
    val poppedGSSNodes   : HashMap<GSSNode, HashSet<SPPFNode?>> = HashMap()
    val createdGSSNodes  : HashMap<GSSNode, GSSNode>            = HashMap()
    val createdSPPFNodes : HashMap<SPPFNode, SPPFNode>          = HashMap()
    val parseResult      : HashMap<Int, HashMap<Int, SPPFNode>> = HashMap()
    
    fun getOrCreateGSSNode(nonterminal : Nonterminal, inputPosition : GraphNode, weight : Int) : GSSNode
    {
        val gssNode = GSSNode(nonterminal, inputPosition, weight)
        
        if (createdGSSNodes.containsKey(gssNode)) {
            if (createdGSSNodes.getValue(gssNode).minWeightOfLeftPart > weight) {
                createdGSSNodes.getValue(gssNode).minWeightOfLeftPart = weight
            }
        } else {
            createdGSSNodes[gssNode] = gssNode
        }

        return createdGSSNodes[gssNode]!!
    }

    fun parse() : HashMap<Int, HashMap<Int, SPPFNode>>
    {
        for (graphNode in startGraphNodes) {
            val descriptor =
                    Descriptor(
                        startState,
                        getOrCreateGSSNode(startState.nonterminal, graphNode, 0),
                        null,
                        graphNode
                    )
            addDescriptor(descriptor)
        }

        while (stack.isNotEmpty()) {
            val descriptor = stack.next()
            parse(descriptor)
        }

        return parseResult
    }

    // Same as handleDescriptor
    fun parse(curDescriptor : Descriptor)
    {
        var curSPPFNode = curDescriptor.sppfNode
        val state       = curDescriptor.rsmState
        val pos         = curDescriptor.inputPosition
        val weight      = curDescriptor.weight
        val gssNode     = curDescriptor.gssNode
        
        addDescriptorHandled(curDescriptor)
        
        if (state.isStart && state.isFinal)
            curSPPFNode = getNodeP(state, curSPPFNode, getOrCreateItemSPPFNode(state, pos, pos))

        for (kvp in state.outgoingTerminalEdges) {
            for (target in kvp.value) {
                val rsmEdge = RSMTerminalEdge(kvp.key, target)
                
                if (pos.outgoingEdges.containsKey(rsmEdge.terminal.value)) {
                    for (head in pos.outgoingEdges[rsmEdge.terminal.value]!!) {
                        val descriptor =
                                Descriptor(
                                    rsmEdge.head,
                                    gssNode,
                                    getNodeP(
                                        rsmEdge.head,
                                        curSPPFNode,
                                        // TODO: Check if we need to add edge weight in addition to descriptor one
                                        getOrCreateTerminalSPPFNode(rsmEdge.terminal, pos, head.targetNode, weight + head.weight)
                                    ),
                                    head.targetNode
                                )
                        addDescriptor(descriptor)
                    }
                }
            }
        }

        for (kvp in state.outgoingNonterminalEdges) {
            for (target in kvp.value) {
                val descriptor =
                        Descriptor(
                            target.nonterminal.startState,
                            createGSSNode(target.nonterminal, target, gssNode, curSPPFNode, pos),
                            null,
                            pos
                        )
                addDescriptor(descriptor)
            }
        }
        
        // null represents Epsilon "Terminal"
        val errorRecoveryEdges = HashMap<Terminal?, TerminalEdgeTarget>()
        
        for (kvp in pos.outgoingEdges) {
            for (targetEdge in kvp.value) {
                val currentTerminal = Terminal(kvp.key)
                
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
                errorRecoveryEdges[null] = TerminalEdgeTarget(targetEdge.targetNode, 1)
            }
        }
        
        for (kvp in errorRecoveryEdges) {
            if (kvp.key == null) {
                handleTerminalOrEpsilonEdge(curDescriptor, null, kvp.value, curDescriptor.rsmState)
            } else {
                // No need for emptiness check, since for empty set
                // the iteration will not fire
                if (state.outgoingTerminalEdges.containsKey(kvp.key)) {
                    for (targetState in state.outgoingTerminalEdges[kvp.key]!!) {
                        handleTerminalOrEpsilonEdge(curDescriptor, kvp.key,  kvp.value, targetState)
                    }
                }
            }
        }
        
        

        if (state.isFinal) pop(gssNode, curSPPFNode, pos)
    }
    
    fun handleTerminalOrEpsilonEdge
    (
        curDescriptor : Descriptor,
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
                        curDescriptor.sppfNode,
                        getOrCreateTerminalSPPFNode(
                            terminal,
                            curDescriptor.inputPosition,
                            targetEdge.targetNode,
                            curDescriptor.weight + targetEdge.weight
                        )
                    ),
                    targetEdge.targetNode
                )
        addDescriptor(descriptor)
    }

    fun pop(gssNode : GSSNode, sppfNode : SPPFNode?, pos : GraphNode)
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
        pos          : GraphNode,
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

        val parent : ParentSPPFNode =
            if (state.isFinal) getOrCreateSymbolSPPFNode(state.nonterminal, leftExtent, rightExtent)
            else               getOrCreateItemSPPFNode(state, leftExtent, rightExtent)

        val packedNode = PackedSPPFNode(nextSPPFNode.leftExtent, state, sppfNode, nextSPPFNode)
        
        sppfNode?.parents?.add(packedNode)
        nextSPPFNode.parents.add(packedNode)
        
        packedNode.parents.add(parent)
        parent.kids.add(packedNode)

        // Define weight of parent node as minimum of kids' weights
        parent.kids.forEach {parent.weight = minOf(parent.weight, it.weight)}
        updateWeights(parent)
        
        return parent
    }

    fun getOrCreateTerminalSPPFNode
    (
        terminal    : Terminal?,
        leftExtent  : GraphNode,
        rightExtent : GraphNode,
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
        leftExtent  : GraphNode,
        rightExtent : GraphNode
    )
        : ParentSPPFNode
    {
        val node = ItemSPPFNode(state, leftExtent, rightExtent)

        if (!createdSPPFNodes.containsKey(node)) {
            createdSPPFNodes[node] = node
        }

        return createdSPPFNodes[node]!! as ParentSPPFNode
    }

    // TODO: Implement weights
    fun getOrCreateSymbolSPPFNode
    (
        nonterminal : Nonterminal,
        leftExtent  : GraphNode,
        rightExtent : GraphNode,
    )
        : SymbolSPPFNode
    {
        val node = SymbolSPPFNode(nonterminal, leftExtent, rightExtent)

        if (!createdSPPFNodes.containsKey(node)) createdSPPFNodes[node] = node

        val result = createdSPPFNodes[node]!! as SymbolSPPFNode

        if (nonterminal == startState.nonterminal && leftExtent.isStart && rightExtent.isFinal) {
            if (!parseResult.containsKey(leftExtent.id)) {
                parseResult[leftExtent.id] = HashMap()
            }

            parseResult[leftExtent.id]!![rightExtent.id] = result
        }
        
        return result
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
        
        return handledDescriptor != null && handledDescriptor.weight <= descriptor.weight
    }
    
    fun addDescriptorHandled(descriptor : Descriptor)
    {
        descriptor.gssNode.handledDescriptors.add(descriptor)
    }
    
    fun updateWeights(sppfNode : ISPPFNode)
    {
        var curNode : ISPPFNode
        
        val visited = HashSet<ISPPFNode>()
        val stack   
        
        
        while (stack.isNotEmpty()) {
            curNode   = stack.removeLast()
            
            for (parent in curNode.parents) {
                when (parent) {
                    is PackedSPPFNode ->
                        parent.weight += curNode.weight
                    is ParentSPPFNode ->
                        parent.kids.forEach { parent.weight = minOf(parent.weight, it.weight) }
                }
                
                if (!visited.contains(parent)) {
                    stack.addLast(parent)
                }
            }
            
            visited.add(curNode)
        }
    }
}
