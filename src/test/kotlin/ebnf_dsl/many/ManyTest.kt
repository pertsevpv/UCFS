package ebnf_dsl.many

import ebnf_dsl.DslTest
import org.junit.jupiter.api.Test
import org.srcgll.dsl.*


class ManyTest : DslTest{
    class StarTest : Grammar() {
        var S by NT()
        init {
            S = Many(Term("a")) * Many(Term("a") * Term("b"))
            setStart(S)
        }
    }

    override fun getFilesPath(): String = "many/"

    @Test
    fun someTest() {
        val rsm = StarTest().toRsm()
//        generateOutput("many.txt", toString(rsm))
//        generateOutput("many.dot", toDot(rsm))
    }
}
