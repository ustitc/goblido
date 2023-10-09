import java.time.LocalDate

fun toggleTask(document: Document, lineNumber: LineNumber): Document {
    val line = document.line(lineNumber)
    val updatedTask = when (val task = task(line)) {
        BlankTask -> return document
        is DoneTask -> task.undo()
        is TodoTask -> task.complete(LocalDate.now())
    }

    val updatedLine = line.copy(text = updatedTask.print())
    return document.changeLine(updatedLine)
}