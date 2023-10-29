import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import kotlin.io.path.Path

class CommandsKtTest : StringSpec(body = {

    "completes task" {
        val content = Document(
            path = Path("todo.txt"),
            name = "todo.txt",
            text = """
                Task 1
                Task 2
                Task 3
            """.trimIndent(),
        )
        val lineNumber = LineNumber(2)

        val updatedContent = toggleTask(document = content, lineNumber = lineNumber)
        updatedContent.line(lineNumber).text shouldStartWith "x"
    }

    "undo task" {
        val content = Document(
            path = Path("todo.txt"),
            name = "todo.txt",
            text = """
                Task 1
                Task 2
                x Task 3
            """.trimIndent(),
        )
        val lineNumber = LineNumber(2)

        val updatedContent = toggleTask(document = content, lineNumber = lineNumber)
        updatedContent.line(lineNumber).text shouldNotStartWith "x"
    }
},)
