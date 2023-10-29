import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TodoEditorTest : StringSpec(body = {

    "moves line up" {
        val textField = TextFieldValue(
            text = """
                first task
                second task
                third task
            """.trimIndent(),
            selection = TextRange(13),
        )

        moveLineUp(textField, LineNumber(1)) shouldBe TextFieldValue(
            text = """
                second task
                first task
                third task
            """.trimIndent(),
            selection = TextRange(2),
        )
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

        moveLineUp(textField, LineNumber(0)) shouldBe textField
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

        moveLineDown(textField, LineNumber(1)) shouldBe TextFieldValue(
            text = """
                first task
                third task
                second task
            """.trimIndent(),
            selection = TextRange(24),
        )
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

        moveLineDown(textField, LineNumber(2)) shouldBe textField
    }
},)
