object DateHighlightPlugin : HighlightPlugin {
    override val regex: Regex
        get() = Regex("[0-9]{4}-[0-9]{2}-[0-9]{2}")
}
