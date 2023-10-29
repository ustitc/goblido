import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.notExists
import kotlin.io.path.writeText

private const val APP_CONFIG_NAME = "app.json"
private const val CONFIG_DIR = ".goblido"
private const val THEMES_DIR = "themes"
private const val USER_HOME = "user.home"
private val defaultTheme = lightTheme.name

@Serializable
data class Config(
    @Serializable(with = PathSerializer::class)
    val lastFile: Path?,
    val theme: ThemeName,
)

@Serializable
data class Theme(
    val name: ThemeName,
    val text: TextConfig,
    @Serializable(with = ColorSerializer::class)
    val backgroundColor: Color,
    @Serializable(with = ColorSerializer::class)
    val cursorColor: Color,
    val project: TextConfig,
    val priority: TextConfig,
    val context: TextConfig,
    val doneTask: TextConfig,
    val link: TextConfig,
    val special: TextConfig,
    val other: TextConfig,
    val select: SelectConfig,
    val sidebar: SidebarStyle,
)

@Serializable
data class TextConfig(
    @Serializable(with = ColorSerializer::class)
    val color: Color,
    val fontWeight: Int,
)

@Serializable
data class SelectConfig(
    @Serializable(with = ColorSerializer::class)
    val backgroundColor: Color,
    @Serializable(with = ColorSerializer::class)
    val handleColor: Color,
)

@Serializable
data class SidebarStyle(
    @Serializable(with = ColorSerializer::class)
    val backgroundColor: Color,
    @Serializable(with = ColorSerializer::class)
    val handleColor: Color,
    @Serializable(with = ColorSerializer::class)
    val textColor: Color,
)

fun loadConfig(json: Json): Config {
    val homeDir = Path(System.getProperty(USER_HOME))

    val configDirPath = homeDir.resolve(CONFIG_DIR)
    if (configDirPath.notExists()) {
        configDirPath.createDirectory()
    }

    val themesDirPath = configDirPath.resolve(THEMES_DIR)
    if (themesDirPath.notExists()) {
        themesDirPath.createDirectory()
    }

    val themes = themesDirPath.toFile().listFiles()
    if (themes?.isEmpty() == true) {
        val lightTheme = json.encodeToString(lightTheme)
        val darkTheme = json.encodeToString(darkTheme)

        themesDirPath.resolve("light.json").writeText(lightTheme)
        themesDirPath.resolve("dark.json").writeText(darkTheme)
    }

    val configFile = configDirPath.resolve(APP_CONFIG_NAME).toFile()
    return if (configFile.exists()) {
        json.decodeFromString<Config>(configFile.readText())
    } else {
        Config(null, defaultTheme).also {
            saveConfig(it, json)
        }
    }
}

fun saveConfig(config: Config, json: Json) {
    val homeDir = Path(System.getProperty(USER_HOME))
    val configDirPath = homeDir.resolve(CONFIG_DIR)
    val configFile = configDirPath.resolve(APP_CONFIG_NAME).toFile()
    val strConfig = json.encodeToString(config)
    configFile.writeText(strConfig)
}

fun loadTheme(name: ThemeName): Theme {
    val homeDir = Path(System.getProperty(USER_HOME))
    val themeFile = homeDir.resolve(CONFIG_DIR)
        .resolve(THEMES_DIR)
        .resolve(name.fileName)
        .toFile()
    return Json.decodeFromString<Theme>(themeFile.readText())
}

fun loadAvailableThemes(): ImmutableList<ThemeName> {
    val homeDir = Path(System.getProperty(USER_HOME))
    return homeDir.resolve(CONFIG_DIR)
        .resolve(THEMES_DIR).listDirectoryEntries()
        .filter { it.isRegularFile() }
        .filter { it.extension == "json" }
        .map { it.name.replace(".json", "") }
        .map { ThemeName(it) }
        .toImmutableList()
}
