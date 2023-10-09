import kotlinx.serialization.*
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.*

private const val appConfigName = "app.json"
private const val configDir = ".goblido"
private const val themesDir = "themes"
private val defaultTheme = ThemeName("light")

private val lightTheme = Theme(
    text = TextConfig(
        color = "#4d4d4d",
        fontWeight = 400
    ),
    backgroundColor = "#ffffff",
    cursorColor = "#527696",
    project = TextConfig(
        color = "#3C6E71",
        fontWeight = 700
    ),
    priority = TextConfig(
        color = "#c16069",
        fontWeight = 700
    ),
    context = TextConfig(
        color = "#2ecc71",
        fontWeight = 700
    ),
    doneTask = TextConfig(
        color = "#7ea8be",
        fontWeight = 400
    ),
    link = TextConfig(
        color = "#0000FF",
        fontWeight = 400
    ),
    special = TextConfig(
        color = "#3498db",
        fontWeight = 700
    ),
    other = TextConfig(
        color = "#3498db",
        fontWeight = 700
    ),
    select = SelectConfig(
        backgroundColor = "#FFEB3B",
        handleColor = "#000000"
    ),
    sidebar = SidebarStyle(
        backgroundColor = "#f0f0f0",
        handleColor = "#b0b0b0",
        textColor = "#000000"
    )
)

private val darkTheme = Theme(
    text = TextConfig(
        color = "#F8F8F2",
        fontWeight = 400
    ),
    backgroundColor = "#282A36",
    cursorColor = "#BD93F9",
    project = TextConfig(
        color = "#FF79C6",
        fontWeight = 700
    ),
    priority = TextConfig(
        color = "#8BE9FD",
        fontWeight = 700
    ),
    doneTask = TextConfig(
        color = "#6272A4",
        fontWeight = 400
    ),
    context = TextConfig(
        color = "#50FA7B",
        fontWeight = 700
    ),
    link = TextConfig(
        color = "#FFD700",
        fontWeight = 400
    ),
    special = TextConfig(
        color = "#FF9800",
        fontWeight = 700
    ),
    other = TextConfig(
        color = "#FF9800",
        fontWeight = 700
    ),
    select = SelectConfig(
        backgroundColor = "#BD93F9",
        handleColor = "#F8F8F2"
    ),
    sidebar = SidebarStyle(
        backgroundColor = "#1c1c1c",
        handleColor = "#6272A4",
        textColor = "#F8F8F2"
    )
)

@Serializable
data class Config(
    @Serializable(with = PathSerializer::class)
    val lastFile: Path?,
    val theme: ThemeName
)

@Serializable
data class Theme(
    val text: TextConfig,
    val backgroundColor: String,
    val cursorColor: String,
    val project: TextConfig,
    val priority: TextConfig,
    val context: TextConfig,
    val doneTask: TextConfig,
    val link: TextConfig,
    val special: TextConfig,
    val other: TextConfig,
    val select: SelectConfig,
    val sidebar: SidebarStyle
)

@Serializable
data class TextConfig(
    val color: String,
    val fontWeight: Int
)

@Serializable
data class SelectConfig(
    val backgroundColor: String,
    val handleColor: String
)

@Serializable
data class SidebarStyle(
    val backgroundColor: String,
    val handleColor: String,
    val textColor: String
)


fun loadConfig(json: Json): Config {
    val homeDir = Path(System.getProperty("user.home"))

    val configDirPath = homeDir.resolve(configDir)
    if (configDirPath.notExists()) {
        configDirPath.createDirectory()
    }

    val themesDirPath = configDirPath.resolve(themesDir)
    if (themesDirPath.notExists()) {
        themesDirPath.createDirectory()
    }

    if (themesDirPath.toFile().listFiles()!!.isEmpty()) {
        val lightTheme = json.encodeToString(lightTheme)
        val darkTheme = json.encodeToString(darkTheme)

        themesDirPath.resolve("light.json").writeText(lightTheme)
        themesDirPath.resolve("dark.json").writeText(darkTheme)
    }

    val configFile = configDirPath.resolve(appConfigName).toFile()
    return if (configFile.exists()) {
        json.decodeFromString<Config>(configFile.readText())
    } else {
        Config(null, defaultTheme).also {
            saveConfig(it, json)
        }
    }
}

fun saveConfig(config: Config, json: Json) {
    val homeDir = Path(System.getProperty("user.home"))
    val configDirPath = homeDir.resolve(configDir)
    val configFile = configDirPath.resolve(appConfigName).toFile()
    val strConfig = json.encodeToString(config)
    configFile.writeText(strConfig)
}

fun loadTheme(name: ThemeName): Theme {
    val homeDir = Path(System.getProperty("user.home"))
    val themeFile = homeDir.resolve(configDir)
        .resolve(themesDir)
        .resolve(name.fileName)
        .toFile()
    return Json.decodeFromString<Theme>(themeFile.readText())
}

fun loadAvailableThemes(): List<ThemeName> {
    val homeDir = Path(System.getProperty("user.home"))
    return homeDir.resolve(configDir)
        .resolve(themesDir).listDirectoryEntries()
        .filter { it.isRegularFile() }
        .filter { it.extension == "json" }
        .map { it.name.replace(".json", "") }
        .map { ThemeName(it) }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Path::class)
object PathSerializer : KSerializer<Path> {
    override val descriptor = buildClassSerialDescriptor("Path") {
        element<String>("pathString")
    }

    override fun serialize(encoder: Encoder, value: Path) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Path {
        return Paths.get(decoder.decodeString())
    }
}
