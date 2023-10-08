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
    
    val weight : Int = (sppfNode?.weight ?: 0) + gssNode.minWeightOfLeftPart
    
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
    fun defaultDescriptorsStackIsNotEmpty() : Boolean
    fun add(descriptor : Descriptor)
    fun next() : Descriptor
}

class ErrorRecoveringDescriptorsStack(size : Int) : IDescriptorsStack
{
    private var createdDescriptors               = Array<HashSet<Descriptor>>(size) { HashSet() }
    private var errorRecoveringDescriptorsStacks = LinkedHashMap<Int, ArrayDeque<Descriptor>>()
    private var defaultDescriptorsStack          = ArrayDeque<Descriptor>()

    /* TODO: Check, maybe we also need to check whether errorRecovery stacks are not empty
             Could use counter variable to track every pushed/removed stack in errorRecovering stacks
     */
    override fun defaultDescriptorsStackIsNotEmpty() = defaultDescriptorsStack.isNotEmpty()

    override fun add(descriptor : Descriptor)
    {
        val pathWeight = descriptor.weight

        // TODO: Think about abandoning duplicate descriptors, some kind of avoiding duplicate descriptors is implemented in GSSNode class
        if (pathWeight == 0) {
//            if (createdDescriptors[descriptor.inputPosition].add(descriptor)) {
//            }
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
        if (defaultDescriptorsStackIsNotEmpty()) {
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
