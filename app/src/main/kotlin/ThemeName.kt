import kotlinx.serialization.Serializable
import java.util.Locale

@JvmInline
@Serializable
value class ThemeName(private val name: String) {

    val displayName: String
        get() {
            return name
                .lowercase()
                .replaceFirstChar { char ->
                    if (char.isLowerCase()) {
                        char.titlecase(Locale.getDefault())
                    } else {
                        char.toString()
                    }
                }
        }

    val fileName: String
        get() {
            return "${name.lowercase()}.json"
        }
}
