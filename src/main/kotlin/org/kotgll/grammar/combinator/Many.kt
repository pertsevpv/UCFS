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

fun Many(termValue: String): Many {
    return Many(Term(termValue))
}

val String.many: Many
    get() = Many(this)

val Regexp.many: Many
    get() = Many(this)

