interface PartsExtension {

    val regex: Regex

}

object DatePartExtension : PartsExtension {
    override val regex: Regex
        get() = Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}")
}