import java.time.LocalDate

@Suppress("ComplexInterface")
sealed interface Task {

    val value: String

    fun print(): String {
        return when (this) {
            BlankTask -> value
            is DoneTask -> if (date == null) {
                "x $value"
            } else {
                "x $date $value"
            }
            is TodoTask -> value
        }
    }

    fun toggle(): Task {
        return when (this) {
            BlankTask -> this
            is DoneTask -> undo(this)
            is TodoTask -> complete(this, LocalDate.now())
        }
    }

    companion object {

        private val doneTaskRegexp = Regex("^x ")
        private val dateRegexp = Regex("^[0-9]{4}-[0-9]{2}-[0-9]{2}")

        fun from(str: String): Task {
            return when {
                str.isBlank() -> BlankTask
                str.contains(doneTaskRegexp) -> parseDoneTask(str)
                else -> TodoTask(str)
            }
        }

        fun tasks(str: String): List<Task> {
            return str
                .split('\n')
                .map { from(it) }
        }

        private fun parseDoneTask(str: String): DoneTask {
            val withoutX = str.replace(doneTaskRegexp, "").trimStart()
            val dateStr = dateRegexp.find(withoutX)?.value
            val date = dateStr?.let { LocalDate.parse(it) }
            val value = withoutX.replace(dateRegexp, "").trimStart()
            return DoneTask(value, date)
        }
    }
}

@JvmInline
value class TodoTask internal constructor(override val value: String) : Task {

    internal fun complete(todo: TodoTask, date: LocalDate): DoneTask {
        return DoneTask(todo.value, date)
    }
}

class DoneTask internal constructor(override val value: String, val date: LocalDate?) : Task {

    internal fun undo(done: DoneTask): TodoTask {
        return TodoTask(done.value)
    }
}

data object BlankTask : Task {
    override val value: String = ""
}
