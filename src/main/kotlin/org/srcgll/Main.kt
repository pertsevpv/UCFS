package org.srcgll

import org.srcgll.grammar.combinator.Grammar
import org.srcgll.grammar.combinator.regexp.NT
import org.srcgll.grammar.combinator.regexp.Term
import org.srcgll.grammar.combinator.regexp.or
import org.srcgll.grammar.combinator.regexp.times
import org.srcgll.rsm.writeRSMToDOT

enum class RecoveryMode {
    ON,
    OFF,
}

enum class Mode {
    Reachability,
    AllPairs,
}

fun main(args: Array<String>) {
    class SGrammar : Grammar() {
        var S by NT()

        init {
            setStart(S)
            S = Term("a") or Term("a") * S or S * S
        }
    }
    writeRSMToDOT(SGrammar().getRsm(), "./rsm.dot")
}
