package org.srcgll

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.srcgll.grammar.readRSMFromTXT
import org.srcgll.grammar.symbol.Terminal
import org.srcgll.input.InputGraph
import org.srcgll.input.LinearInput
import org.srcgll.input.LinearInputLabel
import java.io.*
import org.srcgll.lexer.GeneratedLexer
import org.srcgll.lexer.SymbolCode
import org.srcgll.lexer.Token
import org.srcgll.sppf.SPPF
import org.srcgll.sppf.buildStringFromSPPF

enum class RecoveryMode {
    ON,
    OFF,
}

fun main(args : Array<String>)
{
    val parser = ArgParser("srcgll")

    val recovery by
    parser
        .option(ArgType.Choice<RecoveryMode>(), fullName = "recovery", description = "Recovery mode")
        .default(RecoveryMode.ON)

    val pathToInput by
    parser
        .option(ArgType.String, fullName = "inputPath", description = "Path to input txt file")
        .required()

    val pathToGrammar by
    parser
        .option(ArgType.String, fullName = "grammarPath", description = "Path to grammar txt file")
        .required()

    val pathToOutputString by
    parser
        .option(ArgType.String, fullName = "outputStringPath", description = "Path to output txt file")
        .required()

    val pathToOutputSPPF by
    parser
        .option(ArgType.String, fullName = "outputSPPFPath", description = "Path to output dot file")
        .required()

    parser.parse(args)

//    val inputGraph : InputGraph<Int, Token<SymbolCode>> = LinearInput()
//    var token      : Token<SymbolCode>

    val input    = File(pathToInput).readText().replace("\n","").trim()
    val grammar  = readRSMFromTXT(pathToGrammar)
//    var lexer    = GeneratedLexer(StringReader(input))
    var vertexId = 0

    val inputGraph : InputGraph<Int, LinearInputLabel> = LinearInput()

    inputGraph.addVertex(vertexId)
    inputGraph.startVertex = vertexId

//    while (!lexer.yyatEOF()) {
//        token = lexer.yylex() as Token<SymbolCode>
//        println("(" + token.value + ")")
//        inputGraph.addEdge(vertexId, token, ++vertexId)
//        inputGraph.addVertex(vertexId)
//    }
//    inputGraph.finalVertex = vertexId - 1

    for (x in input) {
        inputGraph.addEdge(vertexId, LinearInputLabel(Terminal(x.toString())), ++vertexId)
        inputGraph.addVertex(vertexId)
    }
    inputGraph.finalVertex = vertexId

    val result  = GLL(grammar, inputGraph, recovery = (recovery == RecoveryMode.ON)).parse()

    SPPF.toDot(result!!, "./result_sppf.dot")

    File(pathToOutputString).printWriter().use {
        out -> out.println(buildStringFromSPPF(result!!))
    }
}
