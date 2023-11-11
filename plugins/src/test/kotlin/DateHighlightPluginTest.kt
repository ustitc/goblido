import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DateHighlightPluginTest : StringSpec(body = {

    "parses dates" {
        val task = Task.from("+project 2023-01-30")

        parts(task, listOf(DateHighlightPlugin)) shouldBe listOf(
            Project("project"),
            PlainText(" "),
            Other("2023-01-30"),
        )
    }
},)
