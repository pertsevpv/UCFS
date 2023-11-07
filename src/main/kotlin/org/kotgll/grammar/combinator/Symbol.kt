package org.kotgll.grammar.combinator

sealed class Symbol : Regexp() {
    override fun derive(symbol: Symbol): Regexp {
        return if (this == symbol) Epsilon else Empty
    }
}