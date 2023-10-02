package org.srcgll.gss

import org.srcgll.descriptors.Descriptor
import org.srcgll.grammar.RSMState
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.sppf.SPPFNode
import java.util.*

class GSSNode
(
    val nonterminal         : Nonterminal,
    val inputPosition       : Int,
    var minWeightOfLeftPart : Int,
)
{
    val edges : HashMap<Pair<RSMState, SPPFNode?>, HashSet<GSSNode>> = HashMap()
    val handledDescriptors = HashSet<Descriptor>()
    
    fun addEdge(rsmState : RSMState, sppfNode : SPPFNode?, gssNode : GSSNode) : Boolean
    {
        val label = Pair(rsmState, sppfNode)
        
        if (!edges.containsKey(label)) edges[label] = HashSet()
        
        return edges[label]!!.add(gssNode)
    }
    
    override fun toString() = "GSSNode(nonterminal=$nonterminal, inputPosition=$inputPosition)"
    
    override fun equals(other : Any?) : Boolean
    {
        if (this === other)                       return true
        
        if (other !is GSSNode)                    return false
        
        if (nonterminal != other.nonterminal)     return false
        
        if (inputPosition != other.inputPosition) return false
        
        return true
    }
    
    val hashCode = Objects.hash(nonterminal, inputPosition)
    override fun hashCode() = hashCode
}
