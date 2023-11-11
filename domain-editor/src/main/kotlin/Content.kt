class Content internal constructor(
    val lines: List<Line>,
    private val visibleLineNumbers: List<LineNumber>,
) {

    init {
        require(visibleLineNumbers.distinct().size == visibleLineNumbers.size)
    }

    private val visibleLines: List<Line>
        get() {
            return visibleLineNumbers.map { lineNumber -> lines[lineNumber.value] }
        }

    operator fun get(lineNumber: LineNumber): Line {
        return get(lineNumber.value)
    }

    operator fun get(index: Int): Line {
        val realLineNumber = visibleLineNumbers[index]
        return lines[realLineNumber.value]
    }

    fun changeText(text: String): Content {
        val textLines = text.lines()
        val visibleLinesCount = visibleLineNumbers.size
        val lastVisibleIndex = visibleLineNumbers
            .getOrNull(visibleLineNumbers.size - 1)?.value

        val linesToReplace = textLines
            .take(visibleLinesCount)
            .mapIndexed { index, str ->
                val realLineNumber = visibleLineNumbers[index]
                Pair(realLineNumber.value, Line(str))
            }
        val linesToAdd = textLines
            .drop(visibleLinesCount)
            .mapIndexed { index, str ->
                val newVisibleIndex = if (lastVisibleIndex == null) {
                    0 + index
                } else {
                    lastVisibleIndex + index + 1
                }
                Pair(newVisibleIndex, Line(str))
            }

        val lines = lines.toMutableList()
        linesToReplace.forEach {
            lines[it.first] = it.second
        }
        linesToAdd.forEach {
            lines.add(it.first, it.second)
        }

        val newLines = linesToReplace + linesToAdd
        val newVisibleLinesIndexes = newLines
            .sortedBy { it.first }
            .map { LineNumber(it.first) }

        if (newVisibleLinesIndexes.size < visibleLinesCount) {
            visibleLineNumbers
                .drop(newVisibleLinesIndexes.size)
                .forEach { lineNumber ->
                    lines.removeAt(lineNumber.value)
                }
        }

        return Content(lines = lines, visibleLineNumbers = newVisibleLinesIndexes)
    }

    fun changeLine(lineNumber: LineNumber, line: Line): Content {
        val realLineNumber = visibleLineNumbers[lineNumber.value]
        val lines = lines.toMutableList()
        lines[realLineNumber.value] = line

        return Content(lines = lines, visibleLineNumbers = visibleLineNumbers)
    }

    fun hide(lineNumbers: List<LineNumber>): Content {
        val indexesToHide = lineNumbers.map { lineNumber -> lineNumber.value }
        val newVisibleLines = visibleLineNumbers.filterIndexed { index, _ ->
            !indexesToHide.contains(index)
        }
        return Content(lines = lines, visibleLineNumbers = newVisibleLines)
    }

    fun moveLineDown(lineNumber: LineNumber): Content {
        val lines = lines.toMutableList()
        val number = lineNumber.value
        if (number < lines.size - 1) {
            lines[number] = lines[number + 1].also { lines[number + 1] = lines[number] }
            return Content(lines = lines, visibleLineNumbers = visibleLineNumbers)
        }
        return this
    }

    fun moveLineUp(lineNumber: LineNumber): Content {
        val lines = lines.toMutableList()
        val number = lineNumber.value
        if (number > 0) {
            lines[number] = lines[number - 1].also { lines[number - 1] = lines[number] }
            return Content(lines = lines, visibleLineNumbers = visibleLineNumbers)
        }
        return this
    }

    fun print(): String {
        return visibleLines.joinToString("\n") { line -> line.text }
    }

    fun showAll(): Content {
        return Content(
            lines = lines,
            visibleLineNumbers = List(lines.size) { index ->
                LineNumber(index)
            },
        )
    }

    companion object {

        fun from(text: String): Content {
            val strLines = text.lines()
            return Content(
                lines = strLines.map { Line(it) },
                visibleLineNumbers = List(strLines.size) { index -> LineNumber(index) },
            )
        }
    }
}
