package org.kotgll.rsm.grammar

import java.io.File

fun writeRSMToTXT(startState: RSMState, pathToTXT: String) {
    File(pathToTXT).printWriter().use { out ->
        out.print(toString(startState))
    }
}

fun toString(startState: RSMState): String {
    val sb = StringBuffer()
    val states: ArrayList<RSMState> = ArrayList()

    val queue: ArrayDeque<RSMState> = ArrayDeque(listOf(startState))
    while (!queue.isEmpty()) {
        val v = queue.removeFirst()
        if (!states.contains(v)) states.add(v)
        for (edge in v.outgoingTerminalEdges) {
            if (!states.contains(edge.head)) queue.addLast(edge.head)
        }
        for (edge in v.outgoingNonterminalEdges) {
            if (!states.contains(edge.head)) queue.addLast(edge.head)
            if (!states.contains(edge.nonterminal.startState)) queue.addLast(edge.nonterminal.startState)
        }
    }


    sb.appendLine(
        """StartState(
        |id=${startState.id},
        |nonterminal=Nonterminal("${startState.nonterminal.name}"),
        |isStart=${startState.isStart},
        |isFinal=${startState.isFinal}
        |)"""
            .trimMargin()
            .replace("\n", "")
    )
    states.forEach { state ->
        sb.appendLine(
            """State(
          |id=${state.id},
          |nonterminal=Nonterminal("${state.nonterminal.name}"),
          |isStart=${state.isStart},
          |isFinal=${state.isFinal}
          |)"""
                .trimMargin()
                .replace("\n", "")
        )
    }
    states.forEach { state ->
        state.outgoingTerminalEdges.forEach { edge ->
            sb.appendLine(
                """TerminalEdge(
            |tail=${state.id},
            |head=${edge.head.id},
            |terminal=Terminal("${edge.terminal.value}")
            |)"""
                    .trimMargin()
                    .replace("\n", "")
            )
        }
        state.outgoingNonterminalEdges.forEach { edge ->
            sb.appendLine(
                """NonterminalEdge(
            |tail=${state.id},
            |head=${edge.head.id},
            |nonterminal=Nonterminal("${edge.nonterminal.name}")
            |)"""
                    .trimMargin()
                    .replace("\n", "")
            )
        }
    }
    return sb.toString()
}
