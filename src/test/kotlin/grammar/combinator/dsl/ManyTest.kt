package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.*
import org.kotgll.rsm.grammar.toString

class ManyTest : DslTest {
    class StarTest : Grammar() {
        var S by NT()

        init {
            S = Many(Term("a")) * Many(Term("a") * Term("b"))
            setStart(S)
        }
    }

    @Test
    fun someTest() {
        val rsm = StarTest().toRsm()
        generateOutput("many.txt", toString(rsm))
    }
}
