package org.kotgll.grammar.combinator

data class Concat(internal val head: Regexp, internal val tail: Regexp) : Regexp() {

    /*
    D[s](h.t) = acceptEps(h).D[s](t) | D[s](h).t
     */
    override fun derive(symbol: Symbol): Regexp {
        val newHead = head.derive(symbol)
        if (!head.acceptEpsilon()) {
            return when (newHead) {
                Empty -> Empty
                Epsilon -> tail
                else -> Concat(newHead, tail)
            }
        }
        return when (newHead) {
            Empty -> tail.derive(symbol)
            Epsilon -> Alternative.makeAlternative(tail, tail.derive(symbol))
            else -> Alternative.makeAlternative(Concat(newHead, tail), tail.derive(symbol))
        }
    }
}

infix operator fun Regexp.times(other: Regexp): Concat = Concat(this, other)

infix operator fun CharSequence.times(other: Regexp): Concat = Concat(Term(this.toString()), other)

infix operator fun CharSequence.times(other: CharSequence): Concat =
    Concat(Term(this.toString()), Term(other.toString()))

infix operator fun Regexp.times(other: CharSequence): Concat = Concat(this, Term(other.toString()))