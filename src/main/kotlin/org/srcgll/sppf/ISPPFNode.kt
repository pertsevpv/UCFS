package org.srcgll.sppf

interface ISPPFNode
{
    var id      : Int
    var weight  : Int
    val parents : HashSet<ISPPFNode>
}