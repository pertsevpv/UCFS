package org.srcgll.sppf

interface ISPPFNode
{
    var weight  : Int
    val parents : HashSet<ISPPFNode>
}