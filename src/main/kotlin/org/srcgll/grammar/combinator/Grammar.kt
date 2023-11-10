package org.srcgll.grammar.combinator

import org.srcgll.grammar.combinator.regexp.NT
import org.srcgll.grammar.combinator.regexp.Regexp
import org.srcgll.grammar.RSMState

object GlobalState
{
    private var value = 0
    fun getNextInt() : Int = value++
}

open class Grammar<TerminalType>
{
    val nonTerms = ArrayList<NT<TerminalType>>()

    private var startState       : RSMState<TerminalType>? = null
    private lateinit var startNt : NT<TerminalType>

    fun setStart(expr : Regexp)
    {
        if (expr is NT<*>) {
            startNt = expr as NT<TerminalType>
        } else throw IllegalArgumentException("Only NT object can be start state for Grammar")
    }

    /**
     * Builds or returns a Rsm built earlier for the grammar
     */
    fun getRsm() : RSMState<TerminalType>
    {
        if (startState == null) {
            buildRsm()
        }
        return startState as RSMState
    }

    /**
     * Builds a new Rsm for the grammar
     */
    fun buildRsm() : RSMState<TerminalType>
    {
        nonTerms.forEach { it.buildRsmBox() }
        startState = startNt.getNonterminal()?.startState
        return startState as RSMState<TerminalType>
    }
}
