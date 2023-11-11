import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TextFieldValueOperationsTest : StringSpec({

    "moves line up" {
        val textField = TextFieldValue(
            text = """
                first task
                second task
                third task
            """.trimIndent(),
            selection = TextRange(13),
        )

        textField.moveCursorUp().selection shouldBe TextRange(2)
    }

    "doesn't move line up if is first" {
        val textField = TextFieldValue(
            text = """
                first task
                second task
                third task
            """.trimIndent(),
            selection = TextRange(2),
        )

        textField.moveCursorUp().selection shouldBe TextRange(2)
    }

    "moves line down" {
        val textField = TextFieldValue(
            text = """
                first task
                second task
                third task
            """.trimIndent(),
            selection = TextRange(13),
        )

        textField.moveCursorDown().selection shouldBe TextRange(24)
    }

    "doesn't move line down if is last" {
        val textField = TextFieldValue(
            text = """
                first task
                second task
                third task
            """.trimIndent(),
            selection = TextRange(23),
        )

        textField.moveCursorDown().selection shouldBe TextRange(23)
    }
})
