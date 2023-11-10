package org.srcgll.descriptors

import org.srcgll.grammar.RSMState
import org.srcgll.gss.GSSNode
import org.srcgll.sppf.node.SPPFNode

class Descriptor <VertexType, TerminalType>
(
    val rsmState      : RSMState<TerminalType>,
    val gssNode       : GSSNode<VertexType, TerminalType>,
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
        return other is Descriptor<*,*>             &&
               other.rsmState == rsmState           &&
               other.inputPosition == inputPosition &&
               other.gssNode == gssNode
    }
}

interface IDescriptorsStack <VertexType, TerminalType>
{
    fun defaultDescriptorsStackIsNotEmpty() : Boolean
    fun add(descriptor : Descriptor<VertexType, TerminalType>)
    fun next() : Descriptor<VertexType, TerminalType>
    fun isAlreadyHandled(descriptor : Descriptor<VertexType, TerminalType>) : Boolean
    fun addToHandled(descriptor : Descriptor<VertexType, TerminalType>)
}

class ErrorRecoveringDescriptorsStack <VertexType, TerminalType> : IDescriptorsStack<VertexType, TerminalType>
{
    private var defaultDescriptorsStack          = ArrayDeque<Descriptor<VertexType, TerminalType>>()
    private var errorRecoveringDescriptorsStacks = LinkedHashMap<Int, ArrayDeque<Descriptor<VertexType, TerminalType>>>()
    
    override fun defaultDescriptorsStackIsNotEmpty() = defaultDescriptorsStack.isNotEmpty()

    override fun add(descriptor : Descriptor<VertexType, TerminalType>)
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
    override fun next() : Descriptor <VertexType, TerminalType>
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

    override fun isAlreadyHandled(descriptor : Descriptor<VertexType, TerminalType>) : Boolean
    {
        val handledDescriptor = descriptor.gssNode.handledDescriptors.find { descriptor.hashCode() == it.hashCode() }

        return handledDescriptor != null && handledDescriptor.weight() <= descriptor.weight()
    }

    override fun addToHandled(descriptor : Descriptor<VertexType, TerminalType>)
    {
        descriptor.gssNode.handledDescriptors.add(descriptor)
    }
}

