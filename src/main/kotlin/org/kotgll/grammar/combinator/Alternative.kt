package org.kotgll.grammar.combinator


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