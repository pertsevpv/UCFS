package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.*
import org.kotgll.rsm.grammar.toString

class ManyTest : DslTest {
    class StarTest : Grammar() {
        var S by NT()

        init {
            S = Many("a") * Many("a" * "b")
            setStart(S)
        }
    }

    @Test
    fun someTest() {
        val rsm = StarTest().buildRsm()
        generateOutput("many.txt", toString(rsm))
    }
}
