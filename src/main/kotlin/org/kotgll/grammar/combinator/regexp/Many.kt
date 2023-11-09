package org.kotgll.grammar.combinator.regexp

data class Many(val exp: Regexp) : Regexp {
    override fun derive(symbol: DerivedSymbol): Regexp {
        return when (val newReg = exp.derive(symbol)) {
            Epsilon -> Many(exp)
            Empty -> Empty
            else -> Concat(newReg, Many(exp))
        }
    }
}

val Regexp.many: Many
    get() = Many(this)

