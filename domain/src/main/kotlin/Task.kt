import java.time.LocalDate

private val doneTaskRegexp = Regex("^x ")
private val dateRegexp = Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2}")
private val taskRegexp = Regex("(\\([A-Z]\\))|\\B\\+(\\S+)|\\B@(\\S+)|(https?://\\S+)|(\\S+:\\S+)")


sealed interface Task : Printable {

    val value: String

}

@JvmInline
value class TodoTask(override val value: String) : Task {

    override fun print(): String = value

    fun complete(date: LocalDate): DoneTask {
        return DoneTask(value, date)
    }

    fun parts(): List<Part> {
        return parts(taskRegexp)
    }

    fun parts(extensions: List<PartsExtension>): List<Part> {
        if (extensions.isEmpty()) {
            return parts()
        }
        val regexp = Regex(taskRegexp.pattern + "|" + extensions.map { it.regex.pattern }.joinToString("|") { "($it)" })
        return parts(regexp)
    }

    private fun parts(regex: Regex): List<Part> {
        val parts = mutableListOf<Part>()
        val result = regex.findAll(value)
        var lastMatchEnd = 0
        for (match in result) {
            val plainTextStart = lastMatchEnd
            val plainTextEnd = match.range.first
            if (plainTextEnd > plainTextStart) {
                val plainText = value.substring(plainTextStart, plainTextEnd)
                parts.add(PlainText(plainText))
            }
            when {
                match.groups[1] != null -> parts.add(Priority(match.value.drop(1).dropLast(1)))
                match.groups[2] != null -> parts.add(Project(match.value.drop(1)))
                match.groups[3] != null -> parts.add(Context(match.value.drop(1)))
                match.groups[4] != null -> parts.add(WebLink(match.value))
                match.groups[5] != null -> {
                    val specialParts = match.value.split(":", limit = 2)
                    parts.add(Special(specialParts[0], specialParts[1]))
                }
                else -> parts.add(Other(match.value))
            }

            lastMatchEnd = match.range.last + 1
        }
        if (lastMatchEnd < value.length) {
            val remainingPlainText = value.substring(lastMatchEnd)
            parts.add(PlainText(remainingPlainText))
        }
        return parts
    }
}

data class DoneTask(override val value: String, private val date: LocalDate?) : Task {

    override fun print(): String {
        return if (date == null) {
            "x $value"
        } else {
            "x $date $value"
        }
    }

    fun undo(): TodoTask {
        return TodoTask(value)
    }

}

object BlankTask : Task {
    override val value: String = ""
    override fun print(): String = ""
}

fun tasks(str: String): List<Task> {
    return str
        .split('\n')
        .map { parseTask(it) }
}

fun task(line: Line): Task {
    val str = line.text
    return parseTask(str)
}

private fun parseTask(str: String): Task {
    return when {
        str.isBlank() -> BlankTask
        str.contains(doneTaskRegexp) -> parseDoneTask(str)
        else -> TodoTask(str)
    }
}

private fun parseDoneTask(str: String): DoneTask {
    val withoutX = str.replace(doneTaskRegexp, "").trimStart()
    val date = dateRegexp.find(withoutX)?.value?.let { LocalDate.parse(it) }
    val value = withoutX.replace(dateRegexp, "").trimStart()
    return DoneTask(value, date)
}
