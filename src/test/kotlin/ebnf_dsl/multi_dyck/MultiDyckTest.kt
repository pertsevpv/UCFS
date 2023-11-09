package ebnf_dsl.multi_dyck

import ebnf_dsl.DslTest
import org.junit.jupiter.api.Test
import org.srcgll.dsl.*

class MultiDyckTest : DslTest{
    class MultiDyckGrammar : Grammar() {
        var S  by NT()
        var S1 by NT()
        var S2 by NT()
        var S3 by NT()
        init {
            S = S1 or S2 or S3 or Epsilon
            S1 = "(" * S * ")" * S
            S2 = "[" * S * "]" * S
            S3 = "{" * S * "}" * S
            setStart(S)
        }
    }

    override fun getFilesPath(): String = "multi_dyck/"

    @Test
    fun someTest() {
        val rsm = MultiDyckGrammar().toRsm()
//        generateOutput("MultiDyckGenerated.txt", toString(rsm))
//        generateOutput("MultiDyckGenerated.dot", toDot(rsm))
    }
}
