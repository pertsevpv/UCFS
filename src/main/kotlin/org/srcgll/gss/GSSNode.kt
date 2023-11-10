package org.srcgll.gss

import org.srcgll.descriptors.Descriptor
import org.srcgll.grammar.RSMState
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.sppf.node.SPPFNode
import java.util.*

class GSSNode <VertexType, TerminalType>
(
    val nonterminal         : Nonterminal<TerminalType>,
    val inputPosition       : VertexType,
    var minWeightOfLeftPart : Int,
)
{
    val edges : HashMap<Pair<RSMState<TerminalType>, SPPFNode<VertexType>?>, HashSet<GSSNode<VertexType, TerminalType>>> = HashMap()
    val handledDescriptors : HashSet<Descriptor<VertexType, TerminalType>> = HashSet()
    
    fun addEdge(rsmState : RSMState<TerminalType>, sppfNode : SPPFNode<VertexType>?, gssNode : GSSNode<VertexType, TerminalType>) : Boolean
    {
        val label = Pair(rsmState, sppfNode)
        
        if (!edges.containsKey(label)) edges[label] = HashSet()
        
        return edges.getValue(label).add(gssNode)
    }
    
    override fun toString() = "GSSNode(nonterminal=$nonterminal, inputPosition=$inputPosition)"
    
    override fun equals(other : Any?) : Boolean
    {
        if (this === other)                       return true
        if (other !is GSSNode<*,*>)                 return false
        if (nonterminal != other.nonterminal)     return false
        if (inputPosition != other.inputPosition) return false

        return true
    }
    
    val hashCode = Objects.hash(nonterminal, inputPosition)
    override fun hashCode() = hashCode
}
