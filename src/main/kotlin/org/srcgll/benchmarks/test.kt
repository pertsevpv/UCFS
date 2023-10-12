//package org.srcgll.benchmarks
//
//import kotlinx.cli.ArgParser
//import kotlinx.cli.ArgType
//import kotlinx.cli.default
//import kotlinx.cli.required
//import org.srcgll.GLL
//import org.srcgll.grammar.readRSMFromTXT
//import org.srcgll.sppf.SPPFNode
//import java.io.File
//import kotlin.system.measureNanoTime
//
//fun main(args : Array<String>)
//{
//    val parser = ArgParser("srcgll.benchmarks")
//
//    val pathToInput by
//    parser
//        .option(
//            ArgType.String, fullName = "inputPath", description = "Path to folder with inputs"
//        )
//        .required()
//    val pathToGrammar by
//    parser
//        .option(
//            ArgType.String, fullName = "grammarPath", description = "Path to grammar txt file"
//        )
//        .required()
//    val pathToOutput by
//    parser
//        .option(
//            ArgType.String, fullName = "outputPath", description = "Path to folder with results"
//        )
//        .required()
//
//    parser.parse(args)
//
//    runRSMWithSPPF(pathToInput, pathToGrammar, pathToOutput)
//}
//
//fun runRSMWithSPPF
//            (
//    pathToInput     : String,
//    pathToRSM       : String,
//    pathToOutput    : String,
//)
//{
//    val rsm     = readRSMFromTXT(pathToRSM)
//    val resultPath = pathToOutput + "chart.csv"
//    File(resultPath).writeText("")
//
//    File(pathToInput)
//        .walk()
//        .filter { it.isFile }
//        .forEach { inputPath ->
//            val inputName = inputPath.nameWithoutExtension
//            println("start:: $inputName")
//            val input = File(inputPath.path).readText().replace("\n", "").trim()
//
//
//            var result : SPPFNode?
//
//            val elapsed = measureNanoTime {
//                result = GLL(rsm, input).parse()
//            }
//            val elapsedSeconds = (elapsed.toDouble() / 1_000_000.0).toInt()
//
//            File(resultPath).appendText("x: $inputName y: $elapsedSeconds\n")
//        }
//}
