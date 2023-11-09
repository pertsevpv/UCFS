package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.Grammar
import org.kotgll.grammar.combinator.Many
import org.kotgll.grammar.combinator.NT
import org.kotgll.grammar.combinator.t
import org.kotgll.rsm.grammar.toString

class AManyTest : DslTest {
    class AStar : Grammar() {
        var A by NT()
        var S by NT()

        init {
            A = "a".t
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
