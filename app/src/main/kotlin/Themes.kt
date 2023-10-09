import kotlinx.serialization.Serializable
import java.util.*

@JvmInline
@Serializable
value class ThemeName(private val name: String) {

    val displayName: String get() {
        return name
            .lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    val fileName: String get() {
        return "${name.lowercase()}.json"
    }

}