import java.nio.file.Path

class Document internal constructor(
    val name: String,
    val content: Content,
    internal val path: Path,
) {

    fun print(): String {
        val lines = content.lines
        return lines.joinToString("\n") { line -> line.text }
    }

    fun changeContent(content: Content): Document {
        return Document(
            name = name,
            content = content,
            path = path,
        )
    }
}
