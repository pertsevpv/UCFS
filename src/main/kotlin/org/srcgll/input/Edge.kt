package org.srcgll.input

class Edge <LabelType : ILabel, VertexType>
(
    val label : LabelType,
    val head  : VertexType,
)