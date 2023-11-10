package org.srcgll.descriptors

import org.srcgll.grammar.RSMState
import org.srcgll.gss.GSSNode
import org.srcgll.sppf.node.SPPFNode

class Descriptor <VertexType>
(
    val rsmState      : RSMState,
    val gssNode       : GSSNode<VertexType>,
    val sppfNode      : SPPFNode<VertexType>?,
    val inputPosition : VertexType,
)
{
    val hashCode =
            23 * (23 * (23 * 17 + rsmState.hashCode()) + inputPosition.hashCode()) + gssNode.hashCode()

    fun weight() : Int = (sppfNode?.weight ?: 0) + gssNode.minWeightOfLeftPart
    
    override fun hashCode() = hashCode
    
    override fun equals(other : Any?) : Boolean
    {
        return other is Descriptor<*>               &&
               other.rsmState == rsmState           &&
               other.inputPosition == inputPosition &&
               other.gssNode == gssNode
    }
}

interface IDescriptorsStack <VertexType>
{
    fun defaultDescriptorsStackIsNotEmpty() : Boolean
    fun add(descriptor : Descriptor<VertexType>)
    fun next() : Descriptor<VertexType>
    fun isAlreadyHandled(descriptor : Descriptor<VertexType>) : Boolean
    fun addToHandled(descriptor : Descriptor<VertexType>)
}

class ErrorRecoveringDescriptorsStack <VertexType> : IDescriptorsStack<VertexType>
{
    private var defaultDescriptorsStack          = ArrayDeque<Descriptor<VertexType>>()
    private var errorRecoveringDescriptorsStacks = LinkedHashMap<Int, ArrayDeque<Descriptor<VertexType>>>()
    
    override fun defaultDescriptorsStackIsNotEmpty() = defaultDescriptorsStack.isNotEmpty()

    override fun add(descriptor : Descriptor<VertexType>)
    {
        if (!isAlreadyHandled(descriptor)) {
            val pathWeight = descriptor.weight()

            if (pathWeight == 0) {
                defaultDescriptorsStack.addLast(descriptor)
            } else {
                if (!errorRecoveringDescriptorsStacks.containsKey(pathWeight))
                    errorRecoveringDescriptorsStacks[pathWeight] = ArrayDeque()

                errorRecoveringDescriptorsStacks.getValue(pathWeight).addLast(descriptor)
            }
        }
    }
    override fun next() : Descriptor <VertexType>
    {
        if (defaultDescriptorsStackIsNotEmpty()) {
            return defaultDescriptorsStack.removeLast()
        } else {
            val iterator   = errorRecoveringDescriptorsStacks.keys.iterator()
            val currentMin = iterator.next()
            val result     = errorRecoveringDescriptorsStacks.getValue(currentMin).removeLast()

            if (errorRecoveringDescriptorsStacks.getValue(currentMin).isEmpty())
                errorRecoveringDescriptorsStacks.remove(currentMin)

            return result
        }
    }

    override fun isAlreadyHandled(descriptor : Descriptor<VertexType>) : Boolean
    {
        val handledDescriptor = descriptor.gssNode.handledDescriptors.find { descriptor.hashCode() == it.hashCode() }

        return handledDescriptor != null && handledDescriptor.weight() <= descriptor.weight()
    }

    override fun addToHandled(descriptor : Descriptor<VertexType>)
    {
        descriptor.gssNode.handledDescriptors.add(descriptor)
    }
}

