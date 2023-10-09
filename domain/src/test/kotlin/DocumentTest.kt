import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path

class DocumentTest : StringSpec({

    val content = Document(
        path = Path("local/todo.txt"),
        name = "todo.txt",
        text = """
        Finish the PR +project
        Have a coffee with Bryan @office
    """.trimIndent()
    )

    "changes entire content" {
        val updatedContent = content.changeContent("Do nothing")

        updatedContent.text shouldBe "Do nothing"
    }

    "changes specific line" {
        val line = Line(text = "Have a beer with Bryan", number = LineNumber(1))

        val updatedContent = content.changeLine(line)

        updatedContent.text shouldBe """
        Finish the PR +project
        Have a beer with Bryan
        """.trimIndent()
    }

    "fails if task out-of-bound" {
        val line = Line(text = "Have a beer with Bryan", number = LineNumber(3))

        shouldThrow<IllegalArgumentException> {
            content.changeLine(line)
        }
    }

    "get a line by number" {
        val lineNumber = LineNumber(1)
        val line = content.line(lineNumber)

        line shouldBe Line(text = "Have a coffee with Bryan @office", number = lineNumber)
        line.number shouldBe LineNumber(1)
    }

    "fails to get a line if line is out of bound" {
        shouldThrow<IndexOutOfBoundsException> {
            content.line(LineNumber(5))
        }
    }

    "fails to construct LineNumber with negative value" {
        shouldThrow<IllegalArgumentException> {
            LineNumber(-1)
        }
    }

})
