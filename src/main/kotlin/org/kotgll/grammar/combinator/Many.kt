package org.kotgll.grammar.combinator

data class Many(val exp: Regexp) : Regexp() {
    override fun derive(symbol: Symbol): Regexp {
        return when (val newReg = exp.derive(symbol)) {
            Epsilon -> Many(exp)
            Empty -> Empty
            else -> Concat(newReg, Many(exp))
        }
    }
}