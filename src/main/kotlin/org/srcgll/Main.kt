package org.srcgll

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.srcgll.grammar.readRSMFromTXT
import org.srcgll.input.Graph
import org.srcgll.sppf.toDot
import java.io.*
import org.srcgll.lexer.GeneratedLexer
import org.srcgll.lexer.SymbolCode
import org.srcgll.lexer.Token

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

    val inputGraph : Graph<Int, Token<SymbolCode>> = Graph()
    var token      : Token<SymbolCode>

    val input    = File(pathToInput).readText()
    val grammar  = readRSMFromTXT(pathToGrammar)
    var lexer    = GeneratedLexer(StringReader(input))
    var vertexId = 1

    inputGraph.addVertex(vertexId)
    inputGraph.startVertex = vertexId

    while (!lexer.yyatEOF()) {
        token = lexer.yylex() as Token<SymbolCode>
//        println("(" + token.value + ")")
        inputGraph.addEdge(vertexId, token, ++vertexId)
        inputGraph.addVertex(vertexId)
    }

    inputGraph.finalVertex = vertexId - 1

    val result  = GLL(grammar, inputGraph, recovery = (recovery == RecoveryMode.ON)).parse()

    File(pathToOutputString).printWriter().use {
        out -> out.println(buildStringFromSPPF(result!!))
    }

    toDot(result!!, pathToOutputSPPF)
}
