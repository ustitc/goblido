import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText
import kotlin.io.path.writeText

@OptIn(ExperimentalPathApi::class)
class DocumentRepositoryTest : StringSpec({

    val testDir = createTempDirectory("todoTest")
    val testFilePath = testDir.resolve("todo.txt")
    val sampleTodo = """
        Finish the PR +project
        Have a coffee with Bryan @office
    """.trimIndent()

    beforeSpec {
        testFilePath.toFile().writeText(sampleTodo)
    }

    afterSpec {
        testDir.deleteRecursively()
    }

    "reads content and return Todo instance" {
        val document = getDocument(testFilePath)

        document.print() shouldBe sampleTodo
    }

    "fails if file does not exist" {
        val nonExistentPath = testDir.resolve("nonExistent.txt")

        shouldThrowAny {
            getDocument(nonExistentPath)
        }
    }

    "saves to file" {
        val path = testDir.resolve("to_change.txt")
        path.writeText("Some tasks")

        val document = getDocument(path)
        val updated = document.changeContent(Content.from("No tasks"))
        save(updated)

        path.readText() shouldBe "No tasks"
    }
})
