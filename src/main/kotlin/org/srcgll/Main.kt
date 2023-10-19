package org.srcgll

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.srcgll.grammar.readRSMFromTXT
import org.srcgll.sppf.toDot
import java.io.File

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

    val input   = File(pathToInput).readText().replace("\n", "").trim()
    val grammar = readRSMFromTXT(pathToGrammar)
    val result  = GLL(grammar, input, recovery = (recovery == RecoveryMode.ON)).parse()

    File(pathToOutputString).printWriter().use {
        out -> out.println(buildStringFromSPPF(result!!))
    }

    toDot(result!!, pathToOutputSPPF)
}
