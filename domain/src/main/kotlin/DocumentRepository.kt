import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.readText

fun getDocument(path: Path): Document {
    require(path.exists())

    return runCatching { path.readText() }
        .map { content ->
            Document(
                path = path,
                name = path.name,
                text = content,
            )
        }
        .getOrThrow()
}

fun save(document: Document) {
    val file = document.path.toFile()
    require(file.exists())
    file.writeText(document.text)
}
