package grammar.combinator.dsl

import org.junit.jupiter.api.Test
import org.kotgll.grammar.combinator.Grammar
import org.kotgll.grammar.combinator.regexp.*
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
        var Op by NT()

        init {
            Program = SeqStatement
            SeqStatement = Statement or Statement * Term(";") * SeqStatement
            Statement = Id * Term(":=") * NumExpr or Term("skip") or (
                    Term("print") * Text) or (
                    Term("print") * NumExpr) or (
                    Term("{") * SeqStatement * Term("}")) or (
                    Term("if") * BoolExp * Term("then") * Statement * Term("else") * Statement) or (
                    Term("while") * BoolExp * Term("do") * Statement
                    )
            NumExpr = NumTerm or NumExpr * Term("+") * NumTerm or NumExpr * Term("-") * NumTerm
            NumTerm =
                NumVar or NumTerm * Term("*") * NumVar or NumTerm * Term("/") * NumVar or Term("(") * NumExpr * Term(")")
            BoolExp = BoolTerm or BoolExp * Term("or") * BoolTerm
            BoolTerm =
                BoolVar or Term("(") * BoolExp * Term(")") or Term("not") * BoolExp or BoolTerm * Term("and") * BoolVar
            Id = Lit or Lit * Id
            NumVar = Num or Num * NumVar
            BoolVar = Term("false") or Term("true") or NumExpr * Op * NumExpr
            Op = makeAlternative(listOf("=", "<", ">", "<=", ">="))
            Lit = makeAlternative(
                listOf(
                    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
                    "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
                )
            )

            Num = makeAlternative(listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
            setStart(Program)
            //todo correctly present text
            Text = Term("\"") * Many(Lit or Num) * Term("\"")
        }
    }


    @Test
    fun someTest() {
        val rsm = MultiDyckGrammar().buildRsm()
        generateOutput("while.txt", toString(rsm))
    }
}
