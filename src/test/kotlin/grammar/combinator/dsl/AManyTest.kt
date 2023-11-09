package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.Grammar
import org.kotgll.grammar.combinator.regexp.Many
import org.kotgll.grammar.combinator.regexp.NT
import org.kotgll.grammar.combinator.regexp.Term
import org.kotgll.rsm.grammar.toString

class AManyTest : DslTest {
    class AStar : Grammar() {
        var A = Term("a")
        var S by NT()

        init {
            S = Many(A)
            setStart(S)
        }
    }


    @Test
    fun someTest() {
        val rsm = AStar().buildRsm()
        generateOutput("aMany.txt", toString(rsm))
    }
}
