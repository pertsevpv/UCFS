package org.kotgll.grammar.combinator

import org.kotgll.rsm.grammar.symbol.Nonterminal


sealed class Regexp {
    /*
    Based on Brzozowski derivative
     */
    abstract fun derive(symbol: Symbol): Regexp
    open fun getNonterminal(): Nonterminal? = null

    /*
     Does the expression accept an epsilon
     */
    fun acceptEpsilon(): Boolean {
        return when (this) {
            is Empty -> false
            is Epsilon -> true
            is Symbol -> false
            is Concat -> head.acceptEpsilon() && tail.acceptEpsilon()
            is Alternative -> left.acceptEpsilon() || right.acceptEpsilon()
            is Many -> true
        }
    }

    fun getAlphabet(): Set<Symbol> {
        return when (this) {
            is Empty -> emptySet()
            is Epsilon -> emptySet()
            is Symbol -> setOf(this)
            is Concat -> head.getAlphabet() + tail.getAlphabet()
            is Alternative -> left.getAlphabet() + right.getAlphabet()
            is Many -> exp.getAlphabet()
        }
    }
}

data object Epsilon : Regexp() {
    override fun derive(symbol: Symbol): Regexp = Empty
}

/*
    Regular expression that does not accept any input string.
 */
data object Empty : Regexp() {
    override fun derive(symbol: Symbol): Regexp = this
}

