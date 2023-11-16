package org.srcgll

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.srcgll.rsm.readRSMFromTXT
import org.srcgll.rsm.symbol.Terminal
import org.srcgll.rsm.writeRSMToDOT
import org.srcgll.input.IGraph
import org.srcgll.input.LinearInput
import org.srcgll.input.LinearInputLabel
import java.io.*
import org.srcgll.lexer.GeneratedLexer
import org.srcgll.lexer.SymbolCode
import org.srcgll.lexer.Token
import org.srcgll.sppf.writeSPPFToDOT
import org.srcgll.sppf.buildStringFromSPPF

enum class RecoveryMode
{
    ON,
    OFF,
}

enum class Mode
{
    Reachability,
    AllPairs,
}

fun main(args : Array<String>)
{
    val parser = ArgParser("srcgll")

    val recoveryMode by
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


    val input    = File(pathToInput).readText().replace("\n","").trim()
    val grammar  = readRSMFromTXT(pathToGrammar)
    var lexer    = GeneratedLexer(StringReader(input))
    var token : Token<SymbolCode>
    var vertexId = 0

    val inputGraph = LinearInput<Int, LinearInputLabel>()

    inputGraph.addVertex(vertexId)
    inputGraph.addStartVertex(vertexId)

//    while (!lexer.yyatEOF()) {
//        token = lexer.yylex() as Token<SymbolCode>
//        println("(" + token.value + ")" + token.type.toString())
//        inputGraph.addEdge(vertexId, LinearInputLabel(Terminal(token)), ++vertexId)
//        inputGraph.addVertex(vertexId)
//    }

    for (x in input) {
        inputGraph.addEdge(vertexId, LinearInputLabel(Terminal(x.toString())), ++vertexId)
        inputGraph.addVertex(vertexId)
    }

    val result  = GLL(grammar, inputGraph, recoveryMode).parse()

    writeSPPFToDOT(result.first!!, "./result_sppf.dot")
    writeRSMToDOT(grammar, "./rsm.dot")

    File(pathToOutputString).printWriter().use {
        out -> out.println(buildStringFromSPPF(result.first!!))
    }
}
