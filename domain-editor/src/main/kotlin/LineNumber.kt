@JvmInline
value class LineNumber(val value: Int) : Comparable<LineNumber> {

    init {
        require(value >= 0)
    }

    operator fun compareTo(value: Int): Int {
        return this.value.compareTo(value)
    }

    override operator fun compareTo(other: LineNumber): Int {
        return compareTo(other.value)
    }

    operator fun plus(number: Int): LineNumber {
        return LineNumber(value + number)
    }

    operator fun minus(number: Int): LineNumber {
        return LineNumber(value - number)
    }
}
