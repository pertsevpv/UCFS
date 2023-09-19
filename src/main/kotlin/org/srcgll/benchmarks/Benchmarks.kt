package org.srcgll.benchmarks

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.srcgll.GLL
import org.srcgll.grammar.readRSMFromTXT
import org.srcgll.graph.readGraphFromCSV
import org.srcgll.sppf.SPPFNode
import java.io.File
import kotlin.system.measureNanoTime

fun getResultPath
(
    pathToOutput : String,
    graph        : String,
    grammarMode  : String,
    grammarName  : String,
    sppfMode     : String,
)
    : String
{
    return pathToOutput +
            (if (pathToOutput.endsWith("/")) "" else "/") +
            "${graph}_${grammarMode}_${grammarName}_${sppfMode}.csv"
}

fun main(args : Array<String>)
{
    val parser = ArgParser("kotgll.benchmarks")

    val pathToInput by
    parser
        .option(
            ArgType.String, fullName = "inputPath", description = "Path to folder with graphs"
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
    pathToGraphs    : String,
    pathToRSM       : String,
    pathToOutput    : String,
    warmUpRounds    : Int,
    benchmarkRounds : Int,
)
{
    val rsm     = readRSMFromTXT(pathToRSM)
    val rsmName = File(pathToRSM).nameWithoutExtension
    
    File(pathToGraphs)
        .walk()
        .filter { it.isFile }
        .forEach { graphPath ->
            val graphName = graphPath.nameWithoutExtension
            println("start:: $graphName")
            val graph = readGraphFromCSV(graphPath.path)
            
            val resultPath = getResultPath(pathToOutput, graphName, "rsm", rsmName, "with_sppf")
            File(resultPath).writeText("")
            
            for (warmUp in 1 .. warmUpRounds)
            {
                var result : HashMap<Int, HashMap<Int, SPPFNode>>
                
                val elapsed = measureNanoTime {
                    result = GLL(rsm, graph).parse()
                }
                
                val elapsedSeconds = elapsed.toDouble() / 1_000_000_000.0
                
                var number = 0
                result.keys.forEach { key -> number += result[key]!!.keys.size }
                
                println("warmup:: $graphName $rsmName ${number} $elapsedSeconds")
            }
            
            for (benchmarkAttempt in 1 .. benchmarkRounds)
            {
                var result : HashMap<Int, HashMap<Int, SPPFNode>>
                
                val elapsed = measureNanoTime {
                    result = GLL(rsm, graph).parse()
                }
                val elapsedSeconds = elapsed.toDouble() / 1_000_000_000.0
                
                var number = 0
                result.keys.forEach { key -> number += result[key]!!.keys.size }
                
                println("benchmark:: $graphName $rsmName ${number} $elapsedSeconds")
                File(resultPath).appendText(elapsed.toString() + "\n")
            }
        }
}
