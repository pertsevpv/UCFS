package org.srcgll

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.srcgll.grammar.readRSMFromTXT
import org.srcgll.graph.readGraphFromCSV
import java.io.File

fun main(args : Array<String>)
{
    val parser = ArgParser("srcgll")

    val pathToInput by
    parser
        .option(ArgType.String, fullName = "inputPath", description = "Path to input txt file")
        .required()

    val pathToGrammar by
    parser
        .option(ArgType.String, fullName = "grammarPath", description = "Path to grammar txt file")
        .required()

    val pathToOutput by
    parser
        .option(ArgType.String, fullName = "outputPath", description = "Path to output txt file")
        .required()

    parser.parse(args)
    
    val graph   = readGraphFromCSV(pathToInput)
    val grammar = readRSMFromTXT(pathToGrammar)
    val result  = GLL(grammar, graph).parse()

    File(pathToOutput).printWriter().use { out ->
        result.keys.forEach { tail ->
            result[tail]!!.keys.forEach { head -> out.println("$tail $head") }
        }
    }
}
