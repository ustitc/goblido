import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith

class CommandsKtTest : StringSpec(body = {

    "completes task" {
        val content = Content.from(
            """
                Task 1
                Task 2
                Task 3
            """.trimIndent(),
        )
        val lineNumber = LineNumber(2)

        val updatedContent = toggleTask(content = content, lineNumber = lineNumber)
        updatedContent[lineNumber].text shouldStartWith "x"
    }

    "undo task" {
        val content = Content.from(
            """
                Task 1
                Task 2
                x Task 3
            """.trimIndent(),
        )
        val lineNumber = LineNumber(2)

        val updatedContent = toggleTask(content = content, lineNumber = lineNumber)
        updatedContent[lineNumber].text shouldNotStartWith "x"
    }

    "returns part of task with custom parsing" {
        val task = Task.from("(A) create a web page ^_^ +goblido ^_^")

        val extension = object : HighlightPlugin {

            override val regex: Regex
                get() = Regex("\\^_\\^")
        }

        parts(task, listOf(extension)) shouldBe listOf(
            Priority("A"),
            PlainText(" create a web page "),
            Other("^_^"),
            PlainText(" "),
            Project("goblido"),
            PlainText(" "),
            Other("^_^"),
        )
    }

    "extensions doesn't affect built-in parsers" {
        val task = Task.from("(A) create a web page +goblido")

        val extension = object : HighlightPlugin {

            override val regex: Regex
                get() = Regex("""\B\+(\S+)""")
        }

        parts(task, listOf(extension)) shouldBe listOf(
            Priority("A"),
            PlainText(" create a web page "),
            Project("goblido"),
        )
    }
},)
