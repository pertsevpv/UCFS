package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.*
import org.kotgll.grammar.combinator.regexp.*
import org.kotgll.rsm.grammar.toString

class MultiDyckTest : DslTest {
    class MultiDyckGrammar : Grammar() {
        var S by NT()
        var S1 by NT()
        var S2 by NT()
        var S3 by NT()

        init {
            S = S1 or S2 or S3 or Epsilon
            S1 = Term("(") * S * Term(")") * S
            S2 = Term("[") * S * Term("]") * S
            S3 = Term("{") * S * Term("}") * S
            setStart(S)
        }
    }

    @Test
    fun someTest() {
        val rsm = MultiDyckGrammar().buildRsm()
        generateOutput("MultiDyckGenerated.txt", toString(rsm))
    }
}
