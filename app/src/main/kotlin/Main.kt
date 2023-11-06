import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.json.Json
import java.nio.file.Path

fun main(): Unit = application {
    val json = Json {
        prettyPrint = true
    }

    val initialConfig = loadConfig(json)
    val initialTheme = loadTheme(initialConfig.theme)
    val initialAvailableThemes = loadAvailableThemes()

    Window(title = "Goblido", onCloseRequest = ::exitApplication) {
        var config by remember { mutableStateOf(initialConfig) }
        var theme by remember { mutableStateOf(initialTheme) }
        var availableThemes by remember { mutableStateOf(initialAvailableThemes) }

        App(
            config = config,
            theme = theme,
            themes = availableThemes,
            onDocumentChange = { path ->
                config = config.copy(lastFile = path)
                saveConfig(config, json)
            },
            onThemeChange = { themeName ->
                theme = loadTheme(themeName)
                availableThemes = loadAvailableThemes()
                config = config.copy(theme = themeName)
                saveConfig(config, json)
            },
        )
    }
}

@Suppress("NoNotNullOperator")
@Composable
fun App(
    config: Config,
    theme: Theme,
    themes: ImmutableList<ThemeName>,
    modifier: Modifier = Modifier,
    onDocumentChange: (Path) -> Unit = {},
    onThemeChange: (ThemeName) -> Unit = {},
) {
    val filePath = config.lastFile
    var document by remember { mutableStateOf(filePath?.let { path -> getDocument(path) }) }
    var textRange by remember { mutableStateOf(TextRange(0)) }

    Row(modifier = modifier) {
        LeftSidebar(
            document = document,
            theme = theme,
            themes = themes,
            modifier = Modifier
                .background(theme.sidebar.backgroundColor),
            onDocumentChange = { path ->
                textRange = TextRange(0)
                document = getDocument(path)
                onDocumentChange(path)
            },
            onThemeChange = onThemeChange,
        )

        if (filePath != null) {
            TodoEditor(
                document = document!!,
                textRange = textRange,
                theme = theme,
                modifier = Modifier
                    .background(theme.backgroundColor)
                    .padding(5.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) { updatedContent, updatedRange ->
                textRange = updatedRange
                document = updatedContent
                save(document!!)
            }
        }
    }
}

@Composable
fun ThemePicker(
    theme: Theme,
    themes: ImmutableList<ThemeName>,
    modifier: Modifier = Modifier,
    onThemeChange: (ThemeName) -> Unit = {},
) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = theme.name.displayName, color = theme.sidebar.textColor)
        IconButton(onClick = { isExpanded = true }) {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = theme.sidebar.textColor,
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .background(theme.sidebar.backgroundColor),
        ) {
            themes.forEach { themeName ->
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        onThemeChange(themeName)
                    },
                    content = {
                        Text(
                            text = themeName.displayName,
                            color = theme.sidebar.textColor,
                        )
                    },
                )
            }
        }
    }
}
