package org.srcgll.descriptors

import org.srcgll.grammar.TokenSequence

class TerminalEdgeTarget
(
    val targetPosition : TokenSequence,
    val weight         : Int = 0,
)