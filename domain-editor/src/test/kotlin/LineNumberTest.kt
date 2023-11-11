import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class LineNumberTest : StringSpec({

    "fails to construct LineNumber with negative value" {
        shouldThrow<IllegalArgumentException> {
            LineNumber(-1)
        }
    }
})
