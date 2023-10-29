import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PartsExtensionTest : StringSpec(body = {

    "parses dates" {
        val task = TodoTask("+project 2023-01-30")

        task.parts(listOf(DatePartExtension)) shouldBe listOf(
            Project("project"),
            PlainText(" "),
            Other("2023-01-30"),
        )
    }
},)
