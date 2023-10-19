package org.srcgll.grammar

import org.srcgll.grammar.symbol.Nonterminal

fun buildRSMFromCFG(pathToCFG : String, pathToOutput : String)
{
    val rsmStates      : HashMap<Int, RSMState> = HashMap()
    val startRSMState  : RSMState?              = null
    val nonterminals   : HashSet<Nonterminal>   = HashSet()

    fun makeRSMState
                (
        id          : Int,
        nonterminal : Nonterminal,
        isStart     : Boolean = false,
        isFinal     : Boolean = false
    ) : RSMState
    {
        val y = RSMState(id, nonterminal, isStart, isFinal)

        if (!rsmStates.containsKey(y.hashCode)) rsmStates[y.hashCode] = y

        return rsmStates[y.hashCode]!!
    }

    val startNonterminalRegex =
        """^StartNonterminal\(
        |nonterminal="(?<nonterminalValue>.*)$"
        """
            .trimMargin()
            .replace("\n", "")
            .toRegex()

}