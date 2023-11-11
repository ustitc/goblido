import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.readText

fun getDocument(path: Path): Document {
    require(path.exists())

    return runCatching { path.readText() }
        .map { text ->
            Document(
                path = path,
                name = path.name,
                content = Content.from(text),
            )
        }
        .getOrThrow()
}

fun save(document: Document) {
    val file = document.path.toFile()
    require(file.exists())
    file.writeText(document.print())
}
