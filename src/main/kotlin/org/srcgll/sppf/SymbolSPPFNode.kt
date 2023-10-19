package org.srcgll.sppf

import org.srcgll.grammar.TokenSequence
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Symbol
import java.util.*

class SymbolSPPFNode
(
    val symbol  : Nonterminal,
    leftExtent  : TokenSequence,
    rightExtent : TokenSequence,
)
    : ParentSPPFNode(leftExtent, rightExtent)
{
    override fun toString() =
        "SymbolSPPFNode(leftExtent=$leftExtent, rightExtent=$rightExtent, symbol=$symbol)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)           return true

        if (other !is SymbolSPPFNode) return false

        if (!super.equals(other))     return false

        if (symbol != other.symbol)   return false

        return true
    }

    override val hashCode : Int = Objects.hash(leftExtent, rightExtent, symbol)
    override fun hashCode() = hashCode

    override fun hasSymbol(symbol : Symbol) = this.symbol == symbol
}
