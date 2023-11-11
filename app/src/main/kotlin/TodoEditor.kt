import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Suppress("MagicNumber")
private val defaultSelectionColor = Color(0xFF4286F4)

@Composable
fun TodoEditor(
    content: Content,
    textRange: TextRange,
    theme: Theme,
    modifier: Modifier = Modifier,
    cursorBrush: Brush = SolidColor(Color.Black),
    textSelectionColors: TextSelectionColors = TextSelectionColors(
        handleColor = defaultSelectionColor,
        backgroundColor = defaultSelectionColor.copy(alpha = 0.4f),
    ),
    onValueChange: (Content, TextRange) -> Unit = { _, _ -> },
) {
    val textState = TextFieldValue(content.print(), textRange)

    Box(modifier = modifier) {
        val stateVertical = rememberScrollState()

        CompositionLocalProvider(
            LocalTextSelectionColors provides textSelectionColors,
        ) {
            BasicTextField(
                value = textState,
                onValueChange = { value ->
                    onValueChange(content.changeText(value.text), value.selection)
                },
                modifier = Modifier
                    .verticalScroll(stateVertical)
                    .onPreviewKeyEvent(KeyBindings(textState, content, onValueChange)),
                cursorBrush = cursorBrush,
                textStyle = TextStyle(
                    color = theme.text.color,
                    fontWeight = FontWeight(theme.text.fontWeight),
                ),
                visualTransformation = TodoTxtTransformation(theme),
            )

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(stateVertical),
            )
        }
    }
}

class TodoTxtTransformation(private val theme: Theme) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = buildColors(text.toString()),
            offsetMapping = OffsetMapping.Identity,
        )
    }

    private fun buildColors(text: String): AnnotatedString {
        return buildAnnotatedString {
            val tasks = Task.tasks(text)
            tasks.forEachIndexed { index, task ->
                append(toAnnotatedString(task))
                if (index < tasks.lastIndex) {
                    append("\n")
                }
            }
        }
    }

    private fun toAnnotatedString(task: Task): AnnotatedString {
        return when (task) {
            BlankTask -> AnnotatedString(task.print())
            is DoneTask -> AnnotatedString(
                text = task.print(),
                spanStyle = SpanStyle(
                    color = theme.doneTask.color,
                    fontWeight = FontWeight(theme.doneTask.fontWeight),
                    textDecoration = TextDecoration.LineThrough,
                ),
            )

            is TodoTask -> toAnnotatedString(task)
        }
    }

    private fun toAnnotatedString(task: TodoTask): AnnotatedString {
        return buildAnnotatedString {
            parts(task, listOf(DateHighlightPlugin)).forEach { part ->
                when (part) {
                    is PlainText -> append(part.print())

                    is Context -> withStyle(
                        SpanStyle(
                            color = theme.context.color,
                            fontWeight = FontWeight(theme.context.fontWeight),
                        ),
                    ) {
                        append(part.print())
                    }

                    is Priority -> withStyle(
                        SpanStyle(
                            color = theme.priority.color,
                            fontWeight = FontWeight(theme.priority.fontWeight),
                        ),
                    ) {
                        append(part.print())
                    }

                    is Project -> withStyle(
                        SpanStyle(
                            color = theme.project.color,
                            fontWeight = FontWeight(theme.project.fontWeight),
                        ),
                    ) {
                        append(part.print())
                    }

                    is Special -> withStyle(
                        SpanStyle(
                            color = theme.special.color,
                            fontWeight = FontWeight(theme.special.fontWeight),
                        ),
                    ) {
                        append(part.print())
                    }

                    is WebLink -> withStyle(
                        SpanStyle(
                            color = theme.link.color,
                            fontWeight = FontWeight(theme.link.fontWeight),
                        ),
                    ) {
                        append(part.print())
                    }

                    is Other -> withStyle(
                        SpanStyle(
                            color = theme.other.color,
                            fontWeight = FontWeight(theme.other.fontWeight),
                        ),
                    ) {
                        append(part.print())
                    }
                }
            }
        }
    }
}
