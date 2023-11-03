package org.srcgll.sppf.node

interface ISPPFNode
{
    var id      : Int
    var weight  : Int
    val parents : HashSet<ISPPFNode>

    fun updateWeights()
    {
        val cycle = HashSet<ISPPFNode>()
        val deque = ArrayDeque(listOf(this))
        var curNode : ISPPFNode

        while (deque.isNotEmpty()) {
            curNode = deque.last()

            when (curNode) {
                is ItemSPPFNode<*> -> {
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
                is PackedSPPFNode<*> -> {
                    val oldWeight = curNode.weight
                    val newWeight = (curNode.leftSPPFNode?.weight ?: 0) + (curNode.rightSPPFNode?.weight ?: 0)

                    if (oldWeight > newWeight) {
                        curNode.weight = newWeight

                        curNode.parents.forEach { deque.addLast(it) }
                    }
                }
                is SymbolSPPFNode<*> -> {
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

            if (curNode == deque.last()) deque.removeLast()
        }
    }
}