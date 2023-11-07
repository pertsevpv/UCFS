package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.*
import org.kotgll.rsm.grammar.toString

class WhileTest : DslTest {
    class MultiDyckGrammar : Grammar() {
        var Program by NT()
        var SeqStatement by NT()
        var Statement by NT()
        var NumExpr by NT()
        var NumTerm by NT()
        var BoolExp by NT()
        var BoolTerm by NT()
        var Id by NT()
        var NumVar by NT()
        var BoolVar by NT()
        var Lit = Term("a..z")
        var Num = Term("0..9")
        var Text = Term("...any..text...")

        init {
            Program = SeqStatement
            SeqStatement = Statement or Statement * ";" * SeqStatement
            Statement = Id * ":=" or "skip" or (
                    "print" * Text) or (
                    "print" * NumExpr) or (
                    "{" * SeqStatement * "}") or (
                    "if" * BoolExp * "then" * Statement * "else" * Statement) or (
                    "while" * BoolExp * "do" * Statement
                    )
            NumExpr = NumTerm or NumExpr * "+" * NumTerm or NumExpr * "-" * NumTerm
            NumTerm = NumVar or NumTerm * "*" * NumVar or NumTerm * "/" * NumVar or "(" * NumExpr * ")"
            BoolExp = BoolTerm or BoolExp * "or" * BoolTerm
            BoolTerm = BoolVar or "(" * BoolExp * ")" or "not" * BoolExp or BoolTerm * "and" * BoolVar
            Id = Lit or Lit * Id
            NumVar = Num or Num * NumVar
            BoolVar = "false" or "true"
            setStart(Program)
        }
    }


    @Test
    fun someTest() {
        val rsm = MultiDyckGrammar().toRsm()
        generateOutput("while.txt", toString(rsm))
    }
}
