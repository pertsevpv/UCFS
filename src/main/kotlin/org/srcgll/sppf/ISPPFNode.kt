package org.srcgll.sppf

interface ISPPFNode
{
    var Id      : Int
    var weight  : Int
    val parents : HashSet<ISPPFNode>
}