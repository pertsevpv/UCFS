package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.*
import org.kotgll.grammar.combinator.regexp.Many
import org.kotgll.grammar.combinator.regexp.NT
import org.kotgll.grammar.combinator.regexp.Term
import org.kotgll.grammar.combinator.regexp.times
import org.kotgll.rsm.grammar.toString

class ManyTest : DslTest {
    class StarTest : Grammar() {
        var S by NT()
        val A = Term("a")
        val B = Term("b")
        init {
            S = Many(A) * Many(A * B)
            setStart(S)
        }
    }

    @Test
    fun someTest() {
        val rsm = StarTest().buildRsm()
        generateOutput("many.txt", toString(rsm))
    }
}
