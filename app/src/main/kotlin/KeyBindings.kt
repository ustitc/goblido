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
    private val content: Content,
    private val onValueChange: (Content, TextRange) -> Unit,
) : (KeyEvent) -> Boolean {

    override fun invoke(event: KeyEvent): Boolean {
        return when {
            event.isMetaPressed && event.key == Key.D && event.type == KeyEventType.KeyDown -> {
                val lineNumber = textState.currentLineNumber()
                val updatedContent = toggleTask(content, lineNumber)
                onValueChange(updatedContent, textState.selection)
                true
            }

            event.isShiftPressed && event.isMetaPressed && event.type == KeyEventType.KeyDown -> {
                when (event.key) {
                    Key.DirectionUp -> {
                        val lineNumber = textState.currentLineNumber()
                        val updatedTextState = textState.moveCursorUp()
                        val updatedContent = content.moveLineUp(lineNumber)
                        onValueChange(updatedContent, updatedTextState.selection)
                        true
                    }

                    Key.DirectionDown -> {
                        val lineNumber = textState.currentLineNumber()
                        val updatedTextState = textState.moveCursorDown()
                        val updatedContent = content.moveLineDown(lineNumber)
                        onValueChange(updatedContent, updatedTextState.selection)
                        true
                    }

                    else -> false
                }
            }

            else -> false
        }
    }
}
