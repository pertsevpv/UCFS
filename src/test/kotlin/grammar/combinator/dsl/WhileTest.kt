package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.*
import org.kotgll.grammar.combinator.Many
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
        var Lit by NT()
        var Num by NT()
        var Text by NT()

        init {
            Program = SeqStatement
            SeqStatement = Statement or Statement * ";" * SeqStatement
            Statement = Id * ":=" * NumExpr or "skip" or (
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
            Lit =
                "a" * "b" * "c" * "d" * "e" * "f" * "g" * "h" * "i" * "j" * "k" * "l" * "m" * "n" * "o" * "p" * "q" * "r" * "s" * "t" * "u" * "v" * "w" * "x" * "y" * "z"
            Num = "1" * "2" * "3" * "4" * "5" * "6" * "7" * "8" * "9" * "0"
            setStart(Program)
            //todo correctly present text
            Text = "\"" * Many(Lit or Num) * "\""
        }
    }


    @Test
    fun someTest() {
        val rsm = MultiDyckGrammar().buildRsm()
        generateOutput("while.txt", toString(rsm))
    }
}
