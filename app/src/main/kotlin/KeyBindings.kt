import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

class KeyBindings(
    private val textState: TextFieldValue,
    private val document: Document,
    private val onValueChange: (Document, TextRange) -> Unit,
) : (KeyEvent) -> Boolean {

    @OptIn(ExperimentalComposeUiApi::class)
    override fun invoke(event: KeyEvent): Boolean {
        return when {
            event.isMetaPressed && event.key == Key.D && event.type == KeyEventType.KeyDown -> {
                val lineNumber = currentLineNumber(document.text, textState.selection)
                val updatedContent = toggleTask(document, lineNumber)
                onValueChange(updatedContent, textState.selection)
                true
            }

            event.isShiftPressed && event.isMetaPressed && event.type == KeyEventType.KeyDown -> {
                when (event.key) {
                    Key.DirectionUp -> {
                        val lineNumber = currentLineNumber(document.text, textState.selection)
                        val moveLineUp = moveLineUp(textState, lineNumber)
                        onValueChange(document.changeContent(moveLineUp.text), moveLineUp.selection)
                        true
                    }

                    Key.DirectionDown -> {
                        val lineNumber = currentLineNumber(document.text, textState.selection)
                        val moveLineDown = moveLineDown(textState, lineNumber)
                        onValueChange(document.changeContent(moveLineDown.text), moveLineDown.selection)
                        true
                    }

                    else -> false
                }
            }

            else -> false
        }
    }

    private fun currentLineNumber(text: String, textRange: TextRange): LineNumber {
        val cursorPosition = textRange.start
        val number = text.substring(0, cursorPosition).count { it == '\n' }
        return LineNumber(number)
    }
}

fun moveLineUp(textField: TextFieldValue, lineNumber: LineNumber): TextFieldValue {
    val text = textField.text
    val lines = text.lines().toMutableList()
    val number = lineNumber.value
    if (number > 0) {
        lines[number] = lines[number - 1].also { lines[number - 1] = lines[number] }
        val updatedText = lines.joinToString("\n")
        val updatedCursor = textField.selection.start - lines[number].length - 1
        return textField.copy(text = updatedText, selection = TextRange(updatedCursor))
    }
    return textField
}

fun moveLineDown(textField: TextFieldValue, lineNumber: LineNumber): TextFieldValue {
    val text = textField.text
    val lines = text.lines().toMutableList()
    val number = lineNumber.value
    if (number < lines.size - 1) {
        lines[number] = lines[number + 1].also { lines[number + 1] = lines[number] }
        val updatedText = lines.joinToString("\n")
        val updatedCursor = textField.selection.start + lines[number].length + 1
        return textField.copy(text = updatedText, selection = TextRange(updatedCursor))
    }
    return textField
}
