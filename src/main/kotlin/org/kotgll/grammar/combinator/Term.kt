package org.kotgll.grammar.combinator

import org.kotgll.rsm.grammar.symbol.Terminal

open class Term(private val text: String) : Symbol() {
    val terminal: Terminal = Terminal(text)
}

val String.t: Term
    get() = Term(this)