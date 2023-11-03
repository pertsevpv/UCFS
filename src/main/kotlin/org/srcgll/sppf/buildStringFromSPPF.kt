package org.srcgll.sppf

import org.srcgll.sppf.node.*
fun buildStringFromSPPF(sppfNode : ISPPFNode) : String
{
    val visited : HashSet<ISPPFNode>    = HashSet()
    val stack   : ArrayDeque<ISPPFNode> = ArrayDeque(listOf(sppfNode))
    val result  : StringBuilder         = StringBuilder(" ".repeat(100))
    var curNode : ISPPFNode


    while (stack.isNotEmpty()) {
        curNode = stack.removeLast()
        visited.add(curNode)

        when (curNode) {
            is TerminalSPPFNode<*> -> {
                result.insert(curNode.leftExtent as Int, curNode.terminal?.value ?: "")
            }
            is PackedSPPFNode<*> -> {
                if (curNode.leftSPPFNode != null)
                    stack.add(curNode.leftSPPFNode!!)
                if (curNode.rightSPPFNode != null)
                    stack.add(curNode.rightSPPFNode!!)
            }
            is ParentSPPFNode<*> -> {
                if (curNode.kids.isNotEmpty()) {
                    curNode.kids.findLast { it.leftSPPFNode != curNode && !visited.contains(it) }?.let { stack.add(it) }
                    curNode.kids.forEach { visited.add(it) }
                }
            }
        }

    }
    return result.toString().replace(" ", "").replace("\n", "").trim()
}