import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun TextFieldValue.moveCursorUp(): TextFieldValue {
    val lines = text.lines().toMutableList()
    val number = currentLineNumber().value
    if (number > 0) {
        val updatedCursor = selection.start - lines[number - 1].length - 1
        return copy(selection = TextRange(updatedCursor))
    }
    return this
}

fun TextFieldValue.moveCursorDown(): TextFieldValue {
    val lines = text.lines().toMutableList()
    val number = currentLineNumber().value
    if (number < lines.size - 1) {
        val updatedCursor = selection.start + lines[number + 1].length + 1
        return copy(selection = TextRange(updatedCursor))
    }
    return this
}

fun TextFieldValue.currentLineNumber(): LineNumber {
    val cursorPosition = selection.start
    val number = text.substring(0, cursorPosition).count { it == '\n' }
    return LineNumber(number)
}
