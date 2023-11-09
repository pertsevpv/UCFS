package org.kotgll.grammar.combinator.regexp

import org.kotgll.rsm.grammar.symbol.Terminal

open class Term<T>(value: T) : DerivedSymbol {
    val terminal: Terminal<T> = Terminal(value)
}

