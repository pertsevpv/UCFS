package org.kotgll.grammar.combinator

import org.kotgll.rsm.grammar.RSMState

object GlobalState {
    private var value = 0
    fun getNextInt(): Int = value++
}

open class Grammar {
    val nonTerms = ArrayList<NT>()
    private lateinit var startState: NT
    fun setStart(state: Regexp) {
        if (state is NT) {
            startState = state
        }
    }

    fun toRsm(): RSMState {
        nonTerms.forEach { it.buildRsmBox() }
        return startState.nonTerm.startState
    }
}
