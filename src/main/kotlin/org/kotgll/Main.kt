package org.kotgll

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.kotgll.graph.readGraphFromCSV
import org.kotgll.rsm.grammar.readRSMFromTXT
import java.io.File

enum class InputMode {
    STRING,
    GRAPH,
}

fun main(args: Array<String>) {
    val parser = ArgParser("kotgll")

    val inputMode by
    parser
        .option(ArgType.Choice<InputMode>(), fullName = "input", description = "Input format")
        .required()

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

    if (inputMode == InputMode.STRING) {
        val input = File(pathToInput).readText()
        val grammar = readRSMFromTXT(pathToGrammar)
        val result = org.kotgll.rsm.stringinput.withsppf.GLL(grammar, input).parse()

        File(pathToOutput).printWriter().use { out -> out.println(result != null) }

    } else if (inputMode == InputMode.GRAPH) {
        val graph = readGraphFromCSV(pathToInput)
        val grammar = readRSMFromTXT(pathToGrammar)
        val result = org.kotgll.rsm.graphinput.withsppf.GLL(grammar, graph).parse()

        File(pathToOutput).printWriter().use { out ->
            result.keys.forEach { tail ->
                result[tail]!!.keys.forEach { head -> out.println("$tail $head") }
            }
        }
    }
}
