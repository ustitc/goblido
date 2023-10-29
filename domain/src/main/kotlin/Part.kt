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

data class Special(private val key: String, private val value: String) : Part {
    override fun print(): String = "$key:$value"
}

@JvmInline
value class Other(private val value: String) : Part {
    override fun print(): String = value
}
