package ebnf_dsl.a_many

import ebnf_dsl.DslTest
import org.junit.jupiter.api.Test
import org.srcgll.dsl.*

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

    override fun getFilesPath(): String = "a_many/"

    @Test
    fun someTest() {
        val rsm = AStar().toRsm()
//        generateOutput("aMany.txt", toString(rsm))
//        generateOutput("aMany.dot", toDot(rsm))
    }
}
