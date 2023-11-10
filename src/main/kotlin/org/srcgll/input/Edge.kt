package org.srcgll.input

data class Edge <VertexType, TerminalType, LabelType : ILabel<TerminalType>>
(
    val label : LabelType,
    val head  : VertexType,
)