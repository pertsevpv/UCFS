package org.kotgll.grammar.combinator


import org.kotgll.rsm.grammar.RSMNonterminalEdge
import org.kotgll.rsm.grammar.RSMState
import org.kotgll.rsm.grammar.RSMTerminalEdge
import org.kotgll.rsm.grammar.symbol.Nonterminal
import java.util.*
import kotlin.reflect.KProperty

open class NT : Symbol() {
    private lateinit var nonTerm: Nonterminal
    private lateinit var rsmDescription: Regexp

    private fun getNewState(regex: Regexp): RSMState {
        return RSMState(GlobalState.getNextInt(), nonTerm, false, regex.acceptEpsilon())
    }

    fun buildRsmBox(): RSMState {
        val regexpToProcess = Stack<Regexp>()
        val regexpToRsmState = HashMap<Regexp, RSMState>()
        regexpToRsmState[rsmDescription] = nonTerm.startState

        val alphabet = rsmDescription.getAlphabet()

        regexpToProcess.add(rsmDescription)

        while (!regexpToProcess.empty()) {
            val regexp = regexpToProcess.pop()
            val state = regexpToRsmState[regexp]
            for (symbol in alphabet) {
                val newState = regexp.derive(symbol)
                if (newState !is Empty) {
                    if (!regexpToRsmState.containsKey(newState)) {
                        regexpToProcess.add(newState)
                    }
                    val toState = regexpToRsmState.getOrPut(newState) { getNewState(newState) }
                    when (symbol) {
                        is Term -> {
                            state?.addTerminalEdge(RSMTerminalEdge(symbol.terminal, toState))
                        }

                        is NT -> {
                            if (!symbol::nonTerm.isInitialized) {
                                throw IllegalArgumentException("Not initialized NT used in description of \"${nonTerm.name}\"")
                            }
                            state?.addNonterminalEdge(RSMNonterminalEdge(symbol.nonTerm, toState))
                        }
                    }
                }
            }

        }
        return nonTerm.startState
    }

    override fun getNonterminal(): Nonterminal? {
        return nonTerm
    }

    operator fun setValue(grammar: Grammar, property: KProperty<*>, lrh: Regexp) {
        if (!this::nonTerm.isInitialized) {
            nonTerm = Nonterminal(property.name)
            grammar.nonTerms.add(this)
            rsmDescription = lrh
            nonTerm.startState = RSMState(GlobalState.getNextInt(), nonTerm, true, rsmDescription.acceptEpsilon())
        } else {
            throw Exception("Multiply initialization of NonTerminal ${property.name}")
        }

    }

    operator fun getValue(grammar: Grammar, property: KProperty<*>): Regexp = this
}