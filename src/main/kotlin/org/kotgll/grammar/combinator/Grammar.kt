package org.kotgll.grammar.combinator

import org.kotgll.grammar.combinator.regexp.NT
import org.kotgll.grammar.combinator.regexp.Regexp
import org.kotgll.rsm.grammar.RSMState

object GlobalState {
    private var value = 0
    fun getNextInt(): Int = value++
}

open class Grammar {
    val nonTerms = ArrayList<NT>()

    private var startState: RSMState? = null
    private lateinit var startNt: NT
    fun setStart(expr: Regexp) {
        if (expr is NT) {
            startNt = expr
        } else throw IllegalArgumentException("Only NT object can be start state for Grammar")
    }

    /**
     * Builds or returns a Rsm built earlier for the grammar
     */
    fun getRsm(): RSMState {
        if (startState == null) {
            buildRsm()
        }
        return startState as RSMState
    }

    /**
     * Builds a new Rsm for the grammar
     */
    fun buildRsm(): RSMState {
        nonTerms.forEach { it.buildRsmBox() }
        startState = startNt.getNonterminal()?.startState
        return startState as RSMState
    }
}
