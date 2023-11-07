package grammar.combinator.dsl

import java.io.File

interface DslTest {
    private fun getRootFilesPath(): String = "src/test/kotlin/grammar/combinator/dsl/generated/"

    fun generateOutput(pathToFile: String, actual: String) {
        val fullPathToFile = getRootFilesPath() + pathToFile
        if (!File(fullPathToFile).exists()) {
            File(fullPathToFile).createNewFile()
            File(fullPathToFile).printWriter().use { out ->
                out.println(actual)
            }
        }
    }

}