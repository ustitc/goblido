import androidx.compose.ui.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.file.Path
import java.nio.file.Paths

private const val RGB_LENGTH = 7
private const val RGBA_LENGTH = 9
private const val OPAQUE = 0xFF000000
private const val HEX = 16
private const val INT = 255

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

object ColorSerializer : KSerializer<Color> {
    override val descriptor = buildClassSerialDescriptor("Color") {
        element<String>("colorString")
    }

    override fun serialize(encoder: Encoder, value: Color) {
        val str = "#%02X%02X%02X".format(
            (value.red * INT).toInt(),
            (value.green * INT).toInt(),
            (value.blue * INT).toInt(),
        )
        encoder.encodeString(str)
    }

    override fun deserialize(decoder: Decoder): Color {
        return toColor(decoder.decodeString())
    }
}

fun toColor(str: String): Color {
    require(str.startsWith("#") && (str.length == RGB_LENGTH || str.length == RGBA_LENGTH)) {
        "Invalid color string format. Expected format: #RRGGBB or #AARRGGBB"
    }

    val colorValue = if (str.length == RGB_LENGTH) {
        // If the color string is in #RRGGBB format, assume an alpha value of FF (fully opaque)
        OPAQUE or str.substring(1).toLong(HEX)
    } else {
        // If the color string is in #AARRGGBB format, parse the alpha value from the string
        str.substring(1).toLong(HEX)
    }

    return Color(colorValue)
}
