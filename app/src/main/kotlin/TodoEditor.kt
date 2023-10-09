import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun TodoEditor(
    document: Document,
    textRange: TextRange,
    theme: Theme,
    onValueChange: (Document, TextRange) -> Unit
) {
    val textState by derivedStateOf { TextFieldValue(document.text, textRange) }

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
                    onValueChange(document.changeContent(it.text), it.selection)
                },
                modifier = Modifier
                    .verticalScroll(stateVertical)
                    .onPreviewKeyEvent(KeyBindings(textState, document, onValueChange)),
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
        return buildAnnotatedString {
            val tasks = tasks(text)
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
                task.print(), SpanStyle(
                    color = theme.doneTask.color.toColor(),
                    fontWeight = FontWeight(theme.doneTask.fontWeight),
                    textDecoration = TextDecoration.LineThrough
                )
            )
            is TodoTask -> buildAnnotatedString {
                task.parts(listOf(DatePartExtension)).forEach {
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

                        is Special -> withStyle(
                            SpanStyle(
                                color = theme.special.color.toColor(),
                                fontWeight = FontWeight(theme.special.fontWeight)
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

                        is Other -> withStyle(
                            SpanStyle(
                                color = theme.other.color.toColor(),
                                fontWeight = FontWeight(theme.other.fontWeight)
                            )
                        ) {
                            append(it.print())
                        }
                    }
                }
            }
        }
    }
}
