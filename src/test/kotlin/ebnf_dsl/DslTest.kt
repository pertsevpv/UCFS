package ebnf_dsl

import java.io.File
import kotlin.test.assertEquals

interface DslTest {
    private fun getDelimiter() = "_".repeat(100)
    private fun getRootFilesPath(): String = "src/test/kotlin/ebnf_dsl/"
    fun getFilesPath(): String = ""

    fun generateOutput(pathToFile: String, actual: String){
        val fullPathToFile = getRootFilesPath() + getFilesPath() + pathToFile
        if(!File(fullPathToFile).exists()){
            File(fullPathToFile).createNewFile()
            File(fullPathToFile).printWriter().use { out ->
                out.println(actual)
            }
        }
    }

    fun assertEqualsFiles(pathToFile: String, actual: String) {
        val fullPathToFile = getRootFilesPath() + getFilesPath() + pathToFile
        if(!File(fullPathToFile).exists()){
            File(fullPathToFile).createNewFile()
            File(fullPathToFile).printWriter().use { out ->
                out.println(actual)
                error("Output file was generated")
            }
        }
        var expected = File(fullPathToFile).bufferedReader().readText()
        if (expected.contains(getDelimiter())) {
            expected = expected.split(getDelimiter())[0]
        }

        if (expected != actual) {
            File(fullPathToFile).printWriter().use { out ->
                out.println(expected)
                out.println(getDelimiter())
                out.println(actual)
            }
        }
        assertEquals(expected, actual)


    }

}