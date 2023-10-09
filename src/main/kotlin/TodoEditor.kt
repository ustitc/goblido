import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TodoEditor(
    text: String,
    theme: Theme,
    onValueChange: (String) -> Unit
) {
    var textState by remember { mutableStateOf(TextFieldValue(text)) }

    Box(
        Modifier
            .background(theme.backgroundColor.toColor())
            .padding(5.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {

        val stateVertical = rememberScrollState()

        val customTextSelectionColors = TextSelectionColors(
            handleColor = theme.select.handleColor.toColor(),
            backgroundColor = theme.select.backgroundColor.toColor().copy(alpha = 0.5f)
        )
        CompositionLocalProvider(
            LocalTextSelectionColors provides customTextSelectionColors
        ) {

            BasicTextField(
                value = textState,
                onValueChange = {
                    textState = it
                    onValueChange(it.text)
                },
                modifier = Modifier
                    .verticalScroll(stateVertical)
                    .onPreviewKeyEvent {
                        when {
                            (it.isMetaPressed && it.key == Key.D && it.type == KeyEventType.KeyDown) -> {
                                val currentLine = currentLine(textState)
                                val updatedTask = when (val task = task(currentLine.text)) {
                                    BlankTask -> return@onPreviewKeyEvent false
                                    is DoneTask -> task.undo()
                                    is TodoTask -> task.complete(LocalDate.now())
                                }
                                val taskText = updatedTask.print()

                                val updatedText =
                                    textState.text.substring(0, currentLine.start) + taskText + textState.text.substring(
                                        currentLine.end
                                    )
                                textState = textState.copy(text = updatedText)
                                onValueChange(updatedText)
                                true
                            }

                            (it.isShiftPressed && it.isMetaPressed && it.type == KeyEventType.KeyDown) -> {
                                when (it.key) {
                                    Key.DirectionUp -> {
                                        val line = currentLine(textState)
                                        textState = moveLineUp(textState, line.number)
                                        true
                                    }

                                    Key.DirectionDown -> {
                                        val line = currentLine(textState)
                                        textState = moveLineDown(textState, line.number)
                                        true
                                    }

                                    else -> false
                                }
                            }

                            else -> false
                        }

                    },
                cursorBrush = SolidColor(theme.cursorColor.toColor()),
                textStyle = TextStyle(
                    color = theme.text.color.toColor(),
                    fontWeight = FontWeight(theme.text.fontWeight)
                ),
                visualTransformation = TodoTxtTransformation(theme)
            )

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(stateVertical)
            )
        }
    }
}

class TodoTxtTransformation(private val theme: Theme) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildColors(text.toString()),
            OffsetMapping.Identity
        )
    }

    private fun buildColors(text: String): AnnotatedString {

        fun DoneTask.toAnnotatedString(): AnnotatedString {
            return AnnotatedString(
                print(), SpanStyle(
                    color = theme.doneTask.color.toColor(),
                    fontWeight = FontWeight(theme.doneTask.fontWeight),
                    textDecoration = TextDecoration.LineThrough
                )
            )
        }

        fun TodoTask.toAnnotatedString(): AnnotatedString {
            return buildAnnotatedString {
                parts().forEach {
                    when (it) {
                        is PlainText -> append(it.print())

                        is Context -> withStyle(
                            SpanStyle(
                                color = theme.context.color.toColor(),
                                fontWeight = FontWeight(theme.context.fontWeight)
                            )
                        ) {
                            append(it.print())
                        }

                        is Priority -> withStyle(
                            SpanStyle(
                                color = theme.priority.color.toColor(),
                                fontWeight = FontWeight(theme.priority.fontWeight)
                            )
                        ) {
                            append(it.print())
                        }

                        is Project -> withStyle(
                            SpanStyle(
                                color = theme.project.color.toColor(),
                                fontWeight = FontWeight(theme.project.fontWeight)
                            )
                        ) {
                            append(it.print())
                        }

                        is WebLink -> withStyle(
                            SpanStyle(
                                color = theme.link.color.toColor(),
                                fontWeight = FontWeight(theme.link.fontWeight)
                            )
                        ) {
                            append(it.print())
                        }
                    }
                }
            }
        }

        return buildAnnotatedString {
            val lines = text.split('\n')
            lines.forEachIndexed { index, line ->
                when (val task = task(line)) {
                    BlankTask -> append(line)
                    is DoneTask -> append(task.toAnnotatedString())
                    is TodoTask -> append(task.toAnnotatedString())
                }

                if (index < lines.lastIndex) {
                    append("\n")
                }
            }
        }
    }
}

fun moveLineUp(textField: TextFieldValue, lineNumber: LineNumber): TextFieldValue {
    val lines = textField.text.lines().toMutableList()
    val lineNumber = lineNumber.value
    if (lineNumber > 0) {
        lines[lineNumber] = lines[lineNumber - 1].also { lines[lineNumber - 1] = lines[lineNumber] }
        val updatedText = lines.joinToString("\n")
        val updatedCursor = textField.selection.start - lines[lineNumber].length - 1
        return textField.copy(text = updatedText, selection = TextRange(updatedCursor))
    }
    return textField
}

fun moveLineDown(textField: TextFieldValue, lineNumber: LineNumber): TextFieldValue {
    val lines = textField.text.lines().toMutableList()
    val lineNumber = lineNumber.value
    if (lineNumber < lines.size - 1) {
        lines[lineNumber] = lines[lineNumber + 1].also { lines[lineNumber + 1] = lines[lineNumber] }
        val updatedText = lines.joinToString("\n")
        val updatedCursor = textField.selection.start + lines[lineNumber].length + 1
        return textField.copy(text = updatedText, selection = TextRange(updatedCursor))
    }
    return textField
}

private fun currentLine(textField: TextFieldValue): Line {
    val cursorPosition = textField.selection.start
    val currentLineStart =
        textField.text.lastIndexOf('\n', startIndex = cursorPosition - 1).let { index ->
            if (index == -1) 0 else index + 1
        }
    val currentLineEnd = textField.text.indexOf('\n', startIndex = cursorPosition).let { index ->
        if (index == -1) textField.text.length else index
    }
    val lineNumber = textField.text.substring(0, cursorPosition).count { it == '\n' }
    val text = textField.text.substring(currentLineStart, currentLineEnd)

    return Line(
        text = text,
        number = LineNumber(lineNumber),
        start = currentLineStart,
        end = currentLineEnd
    )
}

private data class Line(val text: String, val number: LineNumber, val start: Int, val end: Int)

@JvmInline
value class LineNumber(val value: Int) {

    init {
        require(value >= 0)
    }
}
