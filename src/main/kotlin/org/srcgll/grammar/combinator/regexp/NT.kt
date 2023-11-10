package org.srcgll.grammar.combinator.regexp

import org.srcgll.grammar.combinator.GlobalState
import org.srcgll.grammar.combinator.Grammar
import org.srcgll.grammar.RSMNonterminalEdge
import org.srcgll.grammar.RSMState
import org.srcgll.grammar.RSMTerminalEdge
import org.srcgll.grammar.symbol.Nonterminal
import org.srcgll.grammar.symbol.Terminal
import java.util.*
import kotlin.reflect.KProperty

open class NT <TerminalType> : DerivedSymbol
{
    private lateinit var nonTerm        : Nonterminal<TerminalType>
    private lateinit var rsmDescription : Regexp

    private fun getNewState(regex : Regexp) : RSMState<TerminalType>
    {
        return RSMState(GlobalState.getNextInt(), nonTerm, isStart = false, regex.acceptEpsilon())
    }

    fun buildRsmBox() : RSMState<TerminalType>
    {
        val regexpToProcess = Stack<Regexp>()
        val regexpToRsmState = HashMap<Regexp, RSMState<TerminalType>>()
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
                        is Term<*> -> {
                            state?.addTerminalEdge(RSMTerminalEdge(symbol.terminal as Terminal<TerminalType>, toState))
                        }

                        is NT<*> -> {
                            if (!symbol::nonTerm.isInitialized) {
                                throw IllegalArgumentException("Not initialized NT used in description of \"${nonTerm.value}\"")
                            }
                            state?.addNonterminalEdge(RSMNonterminalEdge(symbol.nonTerm as Nonterminal<TerminalType>, toState))
                        }
                    }
                }
            }
        }
        return nonTerm.startState
    }

    override fun getNonterminal() : Nonterminal<TerminalType>?
    {
        return nonTerm
    }

    operator fun setValue(grammar : Grammar<TerminalType>, property : KProperty<*>, lrh : Regexp)
    {
        if (!this::nonTerm.isInitialized) {
            nonTerm = Nonterminal(property.name)
            grammar.nonTerms.add(this)
            rsmDescription = lrh
            nonTerm.startState = RSMState(GlobalState.getNextInt(), nonTerm, isStart = true, rsmDescription.acceptEpsilon())
        } else {
            throw Exception("NonTerminal ${property.name} is already initialized")
        }

    }

    operator fun getValue(grammar : Grammar<TerminalType>, property : KProperty<*>) : Regexp = this
}