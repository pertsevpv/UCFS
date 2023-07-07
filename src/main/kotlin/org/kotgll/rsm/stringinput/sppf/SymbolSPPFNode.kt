package org.kotgll.rsm.stringinput.sppf

import org.kotgll.rsm.grammar.symbol.Nonterminal
import org.kotgll.rsm.grammar.symbol.Symbol
import java.util.*

class SymbolSPPFNode
(
    leftExtent: Int,
    rightExtent: Int,
    val symbol: Nonterminal
)
    : ParentSPPFNode(leftExtent, rightExtent)
{
    override fun toString() =
        "SymbolSPPFNode(leftExtent=$leftExtent, rightExtent=$rightExtent, symbol=$symbol)"

    override fun equals(other : Any?) : Boolean
    {
        if (other !is SymbolSPPFNode) return false

        if (!super.equals(other))     return false

        if (this === other)           return true

        return symbol == other.symbol
    }

    override val hashCode : Int = Objects.hash(leftExtent, rightExtent, symbol)
    override fun hashCode() = hashCode

    override fun hasSymbol(symbol : Symbol) = this.symbol == symbol
}
