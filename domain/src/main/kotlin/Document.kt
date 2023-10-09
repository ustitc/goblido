import java.nio.file.Path

@JvmInline
value class LineNumber(val value: Int) {

    init {
        require(value >= 0)
    }
}

data class Line(val text: String, val number: LineNumber)

data class Document(
    val path: Path,
    val name: String,
    val text: String
) {

    fun changeContent(content: String): Document {
        return copy(text = content)
    }

    fun changeLine(line: Line): Document {
        val lines = text.lines().toMutableList()
        require(line.number.value < lines.size)

        lines[line.number.value] = line.text
        val updatedContent = lines.joinToString("\n")
        return copy(text = updatedContent)
    }

    fun line(lineNumber: LineNumber): Line {
        val lines = text.lines().toMutableList()
        return Line(text = lines[lineNumber.value], number = lineNumber)
    }
}