import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import kotlinx.collections.immutable.ImmutableList
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Path
import kotlin.io.path.Path

@Composable
fun LeftSidebar(
    document: Document?,
    theme: Theme,
    themes: ImmutableList<ThemeName>,
    modifier: Modifier = Modifier,
    onDocumentChange: (Path) -> Unit = {},
    onThemeChange: (ThemeName) -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .width(sidebarWidth(isExpanded))
                .fillMaxHeight()
                .background(theme.sidebar.backgroundColor),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(15.dp),
            ) {
                FilePicker(
                    text = "Open todo",
                    onFilePick = { path ->
                        onDocumentChange(path)
                    },
                )

                if (document != null) {
                    Text(
                        text = document.name,
                        fontWeight = FontWeight.Bold,
                        color = theme.sidebar.textColor,
                    )
                }

                ThemePicker(
                    theme = theme,
                    themes = themes,
                    onThemeChange = onThemeChange,
                )
            }
        }

        MenuButton(theme = theme, onClick = { isExpanded = !isExpanded })
    }
}

private fun sidebarWidth(isExpanded: Boolean): Dp {
    return if (isExpanded) {
        250.dp
    } else {
        0.dp
    }
}

@Composable
private fun MenuButton(theme: Theme, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .background(theme.sidebar.backgroundColor),
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint = theme.sidebar.textColor,
        )
    }
}

@Composable
private fun FilePicker(text: String, onFilePick: (Path) -> Unit) {
    var isFileChooserOpen by remember { mutableStateOf(false) }

    Button(
        onClick = {
            isFileChooserOpen = true
        },
    ) {
        Text(text)
    }

    if (isFileChooserOpen) {
        FileDialog(
            onCloseRequest = { result ->
                isFileChooserOpen = false
                if (result != null) {
                    onFilePick(result)
                }
            },
        )
    }
}

@Composable
private fun FileDialog(parent: Frame? = null, onCloseRequest: (result: Path?) -> Unit): Unit = AwtWindow(
    create = {
        object : FileDialog(parent, "Choose a file", LOAD) {
            override fun isMultipleMode(): Boolean = false

            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    val path = Path(directory).resolve(file)
                    onCloseRequest(path)
                }
            }
        }.apply {
            setFilenameFilter { _, name -> name.endsWith(".txt") }
        }
    },
    dispose = FileDialog::dispose,
)
