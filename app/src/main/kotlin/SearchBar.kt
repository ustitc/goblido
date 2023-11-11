import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Suppress("MagicNumber")
private val defaultSelectionColor = Color(0xFF4286F4)

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    cursorBrush: Brush = SolidColor(Color.Black),
    textStyle: TextStyle = TextStyle.Default,
    textSelectionColors: TextSelectionColors = TextSelectionColors(
        handleColor = defaultSelectionColor,
        backgroundColor = defaultSelectionColor.copy(alpha = 0.4f),
    ),
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier.padding(vertical = 5.dp, horizontal = 3.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search Icon",
            modifier = Modifier,
            tint = textStyle.color,
        )
        CompositionLocalProvider(
            LocalTextSelectionColors provides textSelectionColors,
        ) {
            BasicTextField(
                textStyle = textStyle,
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = cursorBrush,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(2.dp),
            )
        }
    }
}
