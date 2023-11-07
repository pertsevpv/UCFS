package org.kotgll.grammar.combinator

data object Epsilon : Regexp() {
    override fun derive(symbol: Symbol): Regexp = Empty
}

/*
    Regular expression that does not accept any input string.
 */
data object Empty : Regexp() {
    override fun derive(symbol: Symbol): Regexp = this
}

data class Alternative(internal val left: Regexp, internal val right: Regexp) : Regexp() {
    companion object {
        fun makeAlternative(left: Regexp, right: Regexp): Regexp {
            if (left is Empty) return right
            if (right is Empty) return left
            if (left is Alternative && (right == left.left || right == left.right)) {
                return left
            }
            if (right is Alternative && (left == right.left || left == right.right)) {
                return right
            }
            return if (left == right) left else Alternative(left, right)
        }
    }

    override fun derive(symbol: Symbol): Regexp {
        return makeAlternative(left.derive(symbol), right.derive(symbol))
    }

}

infix fun Regexp.or(other: Regexp): Regexp = Alternative.makeAlternative(this, other)
infix fun CharSequence.or(other: Regexp): Regexp = Alternative.makeAlternative(Term(this.toString()), other)
infix fun CharSequence.or(other: CharSequence): Regexp =
    Alternative.makeAlternative(Term(this.toString()), Term(other.toString()))

infix fun Regexp.or(other: CharSequence): Regexp = Alternative.makeAlternative(this, Term(other.toString()))

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

data class Many(val exp: Regexp) : Regexp() {
    override fun derive(symbol: Symbol): Regexp {
        return when (val newReg = exp.derive(symbol)) {
            Epsilon -> Many(exp)
            Empty -> Empty
            else -> Concat(newReg, Many(exp))
        }
    }
}

infix operator fun Regexp.times(other: Regexp): Concat = Concat(this, other)

infix operator fun CharSequence.times(other: Regexp): Concat = Concat(Term(this.toString()), other)

infix operator fun CharSequence.times(other: CharSequence): Concat =
    Concat(Term(this.toString()), Term(other.toString()))

infix operator fun Regexp.times(other: CharSequence): Concat = Concat(this, Term(other.toString()))