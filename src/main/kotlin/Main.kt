import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.serialization.json.Json
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString


fun main() = application {
    val json = Json {
        prettyPrint = true
    }

    val initialConfig = loadConfig(json)
    val initialTheme = loadTheme(initialConfig.theme)
    val initialAvailableThemes = loadAvailableThemes()

    Window(title = "Goblido", onCloseRequest = ::exitApplication) {
        var config by remember { mutableStateOf(initialConfig) }
        var theme by remember { mutableStateOf(initialTheme) }
        var activeTheme by remember { mutableStateOf(initialConfig.theme) }
        var availableThemes by remember { mutableStateOf(initialAvailableThemes) }

        App(
            config,
            theme,
            activeTheme,
            availableThemes,
            onTodoChange = { path ->
                config = config.copy(lastFile = path.pathString)
                saveConfig(config, json)
            },
            onThemeChange = { themeName ->
                theme = loadTheme(themeName)
                activeTheme = themeName
                availableThemes = loadAvailableThemes()
                config = config.copy(theme = themeName)
                saveConfig(config, json)
            }
        )
    }
}

@Composable
@Preview
fun App(
    config: Config,
    theme: Theme,
    activeTheme: ThemeName,
    themes: List<ThemeName>,
    onTodoChange: (Path) -> Unit,
    onThemeChange: (ThemeName) -> Unit
) {
    val file by derivedStateOf { config.lastFile?.let { File(it) } }
    var text by remember { mutableStateOf(file?.readText() ?: "") }

    Row {
        LeftSidebar(file, theme, activeTheme, themes, onTodoChange, onThemeChange)

        if (file != null) {
            TodoEditor(text, theme) {
                text = it
                file!!.writeText(text)
            }
        }
    }
}

@Composable
fun LeftSidebar(
    todo: File?,
    theme: Theme,
    activeTheme: ThemeName,
    themes: List<ThemeName>,
    onTodoChange: (Path) -> Unit,
    onThemeChange: (ThemeName) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isFileChooserOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .background(theme.sidebar.backgroundColor.toColor())
    ) {
        Box(
            modifier = Modifier
                .width(if (isExpanded) 250.dp else 0.dp)
                .fillMaxHeight()
                .background(theme.sidebar.backgroundColor.toColor())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(15.dp)
            ) {
                Button(onClick = {
                    isFileChooserOpen = true
                }) {
                    Text("Open todo")
                }

                if (todo != null) {
                    Text(
                        todo.name,
                        fontWeight = FontWeight.Bold,
                        color = theme.sidebar.textColor.toColor()
                    )
                }

                ThemePicker(theme, activeTheme, themes, onThemeChange)
            }
        }
        IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier
                .background(theme.sidebar.backgroundColor.toColor())
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = theme.sidebar.textColor.toColor()
            )
        }

        if (isFileChooserOpen) {
            FileDialog(
                onCloseRequest = { result ->
                    isFileChooserOpen = false
                    if (result != null) {
                        onTodoChange(result)
                    }
                }
            )
        }
    }
}

@Composable
fun ThemePicker(
    theme: Theme,
    activeTheme: ThemeName,
    themes: List<ThemeName>,
    onThemeChange: (ThemeName) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = activeTheme.displayName, color = theme.sidebar.textColor.toColor())
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = theme.sidebar.textColor.toColor()
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(theme.sidebar.backgroundColor.toColor())
        ) {
            themes.forEach { themeName ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onThemeChange(themeName)
                }) {
                    Text(
                        text = themeName.displayName,
                        color = theme.sidebar.textColor.toColor()
                    )
                }
            }
        }
    }
}

@Composable
private fun FileDialog(
    parent: Frame? = null,
    onCloseRequest: (result: Path?) -> Unit
) = AwtWindow(
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
    dispose = FileDialog::dispose
)

fun String.toColor(): Color {
    require(this.startsWith("#") && (this.length == 7 || this.length == 9)) {
        "Invalid color string format. Expected format: #RRGGBB or #AARRGGBB"
    }

    val colorValue = if (length == 7) {
        // If the color string is in #RRGGBB format, assume an alpha value of FF (fully opaque)
        0xFF000000 or substring(1).toLong(16)
    } else {
        // If the color string is in #AARRGGBB format, parse the alpha value from the string
        substring(1).toLong(16)
    }

    return Color(colorValue)
}

