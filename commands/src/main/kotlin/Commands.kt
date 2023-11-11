private val whiteSpaceRegex: Regex = Regex("\\s")

fun toggleTask(content: Content, lineNumber: LineNumber): Content {
    val line = content[lineNumber.value]
    val task = Task.from(line.text)
    val updatedTask = task.toggle()
    val updatedLine = Line(updatedTask.print())
    return content.changeLine(lineNumber, updatedLine)
}

fun parts(task: Task, plugins: List<HighlightPlugin>): List<Part> {
    if (plugins.isEmpty()) {
        return Part.parts(task)
    }
    return Part.parts(task, plugins.map { it.regex })
}

fun search(content: Content, query: String): Content {
    val linesToHide = content.lines
        .mapIndexed { index, line -> Pair(index, line.text) }
        .filter { pair ->
            !isMatching(pair.second, query)
        }
        .map { pair -> pair.first }
        .map { index -> LineNumber(index) }

    return content
        .showAll()
        .hide(linesToHide)
}

private fun isMatching(str: String, query: String): Boolean {
    return str.split(whiteSpaceRegex).any { word -> word.startsWith(query) }
}
