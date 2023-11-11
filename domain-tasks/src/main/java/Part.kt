sealed interface Part {

    fun print(): String {
        return when (this) {
            is Context -> "@$value"
            is Priority -> "($value)"
            is Project -> "+$value"
            is Special -> "$key:$value"
            is PlainText -> value
            is WebLink -> value
            is Other -> value
        }
    }

    companion object {

        private val taskRegexp = Regex("""(\([A-Z]\))|\B\+(\S+)|\B@(\S+)|(https?://\S+)|(\S+:\S+)""")

        fun parts(task: Task): List<Part> {
            return when (task) {
                BlankTask -> emptyList()
                is DoneTask -> parts(task, emptyList())
                is TodoTask -> parts(task, emptyList())
            }
        }

        @Suppress("AvoidVarsExceptWithDelegate", "MagicNumber")
        fun parts(todo: Task, extraRegex: List<Regex>): List<Part> {
            val regex = if (extraRegex.isEmpty()) {
                taskRegexp
            } else {
                Regex(taskRegexp.pattern + "|" + extraRegex.map { it.pattern }.joinToString("|") { "($it)" })
            }
            val parts = mutableListOf<Part>()
            val result = regex.findAll(todo.value)
            var lastMatchEnd = 0
            for (match in result) {
                val plainTextStart = lastMatchEnd
                val plainTextEnd = match.range.first
                if (plainTextEnd > plainTextStart) {
                    val plainText = todo.value.substring(plainTextStart, plainTextEnd)
                    parts.add(PlainText(plainText))
                }
                when {
                    match.groups[1] != null -> {
                        val drop = match.value.drop(1)
                        parts.add(Priority(drop.dropLast(1)))
                    }
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
            if (lastMatchEnd < todo.value.length) {
                val remainingPlainText = todo.value.substring(lastMatchEnd)
                parts.add(PlainText(remainingPlainText))
            }
            return parts
        }
    }
}

@JvmInline
value class PlainText(val value: String) : Part

@JvmInline
value class WebLink(val value: String) : Part

@JvmInline
value class Project(val value: String) : Part

@JvmInline
value class Context(val value: String) : Part

@JvmInline
value class Priority(val value: String) : Part

data class Special(val key: String, val value: String) : Part

@JvmInline
value class Other(val value: String) : Part
