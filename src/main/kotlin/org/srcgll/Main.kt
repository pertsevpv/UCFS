package org.srcgll

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.srcgll.grammar.readRSMFromTXT
import java.io.File

fun main(args : Array<String>)
{
    val parser = ArgParser("srcgll")

    val input by
    parser
        .option(ArgType.String, fullName = "input", description = "Input string")
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

    val grammar = readRSMFromTXT(pathToGrammar)
    val result  = GLL(grammar, input).parse()

    // Output SPPF to file
//    File(pathToOutput).printWriter().use { out ->
//        result.kids.forEach { tail ->
//            result[tail]!!.keys.forEach { head -> out.println("$tail $head") }
//        }
//    }
}
