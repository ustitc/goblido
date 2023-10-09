import java.time.LocalDate

private val doneTaskRegexp = Regex("^x ")
private val dateRegexp = Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2}")
private val taskRegexp = Regex("(\\([A-Z]\\))|\\B\\+(\\S+)|\\B@(\\S+)|(https?://\\S+)")

interface Printable {
    fun print(): String
}

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
        val parts = mutableListOf<Part>()
        val result = taskRegexp.findAll(value)
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

sealed interface Part : Printable

@JvmInline
value class PlainText(private val value: String) : Part {
    override fun print(): String = value
}

@JvmInline
value class WebLink(private val value: String) : Part {
    override fun print(): String = value
}

@JvmInline
value class Project(private val value: String) : Part {
    override fun print(): String = "+$value"
}

@JvmInline
value class Context(private val value: String) : Part {
    override fun print(): String = "@$value"
}

@JvmInline
value class Priority(private val value: String) : Part {
    override fun print(): String = "($value)"
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

fun task(str: String): Task {
    return when {
        str.isBlank() -> BlankTask
        str.contains(doneTaskRegexp) -> parseDoneTask(str)
        else -> TodoTask(str)
    }
}

private fun parseDoneTask(str: String): DoneTask {
    val withoutX = str.replace(doneTaskRegexp, "").trimStart()
    val date = dateRegexp.find(withoutX)?.value.let { LocalDate.parse(it) }
    val value = withoutX.replace(dateRegexp, "").trimStart()
    return DoneTask(value, date)
}
