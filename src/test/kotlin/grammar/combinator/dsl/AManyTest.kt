package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.*
import org.kotgll.rsm.grammar.toString

class AManyTest : DslTest{
    class AStar : Grammar() {
        var A by NT()
        var S by NT()
        init {
            A = Term("a")
            S = Many(A)
            setStart(S)
        }
    }


    @Test
    fun someTest() {
        val rsm = AStar().toRsm()
        generateOutput("aMany.txt", toString(rsm))
    }
}
