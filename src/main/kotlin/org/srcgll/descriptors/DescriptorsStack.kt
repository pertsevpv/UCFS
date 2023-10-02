package org.srcgll.descriptors

import org.srcgll.grammar.RSMState
import org.srcgll.gss.GSSNode
import org.srcgll.sppf.SPPFNode

class Descriptor
(
    val rsmState      : RSMState,
    val gssNode       : GSSNode,
    val sppfNode      : SPPFNode?,
    val inputPosition : Int,
)
{
    val hashCode =
            23 * (23 * (23 * 17 + rsmState.hashCode()) + inputPosition.hashCode()) + gssNode.hashCode()
    
    val weight : Int =
            when (sppfNode) {
                null -> 0
                else -> sppfNode.weight
            } + gssNode.minWeightOfLeftPart
    
    override fun hashCode() = hashCode
    
    override fun equals(other : Any?) : Boolean
    {
        return other is Descriptor                  &&
               other.rsmState == rsmState           &&
               other.inputPosition == inputPosition &&
               other.gssNode == gssNode
    }
}

interface IDescriptorsStack
{
    fun isNotEmpty() : Boolean
    fun add(descriptor : Descriptor)
    fun next() : Descriptor
}

class ErrorRecoveringDescriptorsStack(size : Int) : IDescriptorsStack
{
    private var createdDescriptorStacks          = Array<HashSet<Descriptor>>(size) { HashSet() }
    private var errorRecoveringDescriptorsStacks = LinkedHashMap<Int, ArrayDeque<Descriptor>>()
    private var defaultDescriptorsStack          = ArrayDeque<Descriptor>()

    override fun isNotEmpty() = defaultDescriptorsStack.isNotEmpty()

    override fun add(descriptor : Descriptor)
    {
        val pathWeight = descriptor.weight

        // TODO: Think about abandoning duplicate descriptors
//        if (createdDescriptorStacks[descriptor.inputPosition].add(descriptor)) {
//        }

        if (pathWeight == 0) {
            defaultDescriptorsStack.addLast(descriptor)
        } else {
            if (!errorRecoveringDescriptorsStacks.containsKey(pathWeight)) {
                errorRecoveringDescriptorsStacks[pathWeight] = ArrayDeque()
            }
            errorRecoveringDescriptorsStacks.getValue(pathWeight).addLast(descriptor)
        }

    }
    
    
    
    override fun next() : Descriptor
    {
        if (!defaultDescriptorsStack.isEmpty()) {
            return defaultDescriptorsStack.removeLast()
        } else {
            val iterator = errorRecoveringDescriptorsStacks.keys.iterator()
            val moved    = iterator.hasNext()
            assert(moved)
            
            val currentMin = iterator.next()
            val result = errorRecoveringDescriptorsStacks.getValue(currentMin).removeLast()
            
            if (result.weight > currentMin) {
                throw Error("!!!")
            }
            if (errorRecoveringDescriptorsStacks.getValue(currentMin).isEmpty()) {
                errorRecoveringDescriptorsStacks.remove(currentMin)
            }
            
            return result
        }
    }
}
