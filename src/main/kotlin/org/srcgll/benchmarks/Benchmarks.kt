package org.srcgll.benchmarks

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.srcgll.GLL
import org.srcgll.grammar.readRSMFromTXT
import org.srcgll.sppf.SPPFNode
import java.io.File
import kotlin.system.measureNanoTime

fun getResultPath
(
    pathToOutput : String,
    inputName    : String,
    grammarMode  : String,
    grammarName  : String,
    sppfMode     : String,
)
    : String
{
    return pathToOutput +
            (if (pathToOutput.endsWith("/")) "" else "/") +
            "${inputName}_${grammarMode}_${grammarName}_${sppfMode}.csv"
}

fun main(args : Array<String>)
{
    val parser = ArgParser("srcgll.benchmarks")

    val pathToInput by
    parser
        .option(
            ArgType.String, fullName = "inputPath", description = "Path to folder with inputs"
        )
        .required()
    val pathToGrammar by
    parser
        .option(
            ArgType.String, fullName = "grammarPath", description = "Path to grammar txt file"
        )
        .required()
    val pathToOutput by
    parser
        .option(
            ArgType.String, fullName = "outputPath", description = "Path to folder with results"
        )
        .required()
    val warmUpRounds by
    parser
        .option(ArgType.Int, fullName = "warmUpRounds", description = "Number of warm-up rounds")
        .default(3)
    val benchmarksRounds by
    parser
        .option(
            ArgType.Int, fullName = "benchmarkRounds", description = "Number of benchmark rounds"
        )
        .default(10)

    parser.parse(args)

    runRSMWithSPPF(pathToInput, pathToGrammar, pathToOutput, warmUpRounds, benchmarksRounds)
}

fun runRSMWithSPPF
(
    pathToInput     : String,
    pathToRSM       : String,
    pathToOutput    : String,
    warmUpRounds    : Int,
    benchmarkRounds : Int,
)
{
    val rsm     = readRSMFromTXT(pathToRSM)
    val rsmName = File(pathToRSM).nameWithoutExtension

    File(pathToInput)
        .walk()
        .filter { it.isFile }
        .forEach { inputPath ->
            val inputName = inputPath.nameWithoutExtension
            println("start:: $inputName")
            val input = File(inputPath.path).readText().replace("\n", "").trim()

            val resultPath = getResultPath(pathToOutput, inputName, "rsm", rsmName, "with_sppf")
            File(resultPath).writeText("")

            for (warmUp in 1 .. warmUpRounds)
            {
                var result : SPPFNode?

                val elapsedDefault = measureNanoTime {
                    result = GLL(rsm, input, recovery = false).parse()
                }

                val elapsedRecovery = measureNanoTime {
                    result = GLL(rsm, input).parse()
                }

                val elapsedRecoverySeconds = elapsedRecovery.toDouble() / 1_000_000_000.0
                val elapsedDefaultSeconds  = elapsedDefault.toDouble()  / 1_000_000_000.0

//                var number = 0
//                result.keys.forEach { key -> number += result[key]!!.keys.size }

//                println("warmup:: $inputName $rsmName $elapsedSeconds")
            }

            var totalRecoveryTime = 0.0
            var totalDefaultTime  = 0.0

            for (benchmarkAttempt in 1 .. benchmarkRounds)
            {
                var result : SPPFNode?

                val elapsedDefault = measureNanoTime {
                    result = GLL(rsm, input, recovery = false).parse()
                }

                val elapsedRecovery = measureNanoTime {
                    result = GLL(rsm, input).parse()
                }

                val elapsedRecoverySeconds = elapsedRecovery.toDouble() / 1_000_000_000.0
                val elapsedDefaultSeconds  = elapsedDefault.toDouble()  / 1_000_000_000.0

                totalDefaultTime  += elapsedDefaultSeconds
                totalRecoveryTime += elapsedRecoverySeconds
//                var number = 0
//                result.keys.forEach { key -> number += result[key]!!.keys.size }

                println("benchmark:: $inputName $rsmName $elapsedDefaultSeconds $elapsedRecoverySeconds")
                File(resultPath).appendText("$elapsedDefaultSeconds ::: $elapsedRecoverySeconds\n")
            }
            val averageRecoveryTime = totalRecoveryTime / benchmarkRounds
            val averageDefaultTime  = totalDefaultTime  / benchmarkRounds

            File(resultPath).appendText("totalDefaultTime:  $totalDefaultTime seconds\n")
            File(resultPath).appendText("totalRecoveryTime: $totalRecoveryTime seconds\n")
            File(resultPath).appendText("averageDefaultTime :  $averageDefaultTime seconds\n")
            File(resultPath).appendText("averageRecoveryTime : $averageRecoveryTime seconds\n")
        }
}
