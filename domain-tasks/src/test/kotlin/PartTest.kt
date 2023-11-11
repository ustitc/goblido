import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PartTest : StringSpec({

    "PlainText should print its value" {
        PlainText("hello").print() shouldBe "hello"
    }

    "WebLink should print its value" {
        WebLink("http://example.com").print() shouldBe "http://example.com"
    }

    "Project should prefix its value with +" {
        Project("MyProject").print() shouldBe "+MyProject"
    }

    "Context should prefix its value with @" {
        Context("MyContext").print() shouldBe "@MyContext"
    }

    "Priority should enclose its value in parentheses" {
        Priority("High").print() shouldBe "(High)"
    }

    "Special should print its key and value separated by colon" {
        Special("due", "tomorrow").print() shouldBe "due:tomorrow"
    }

    "Other should print its value" {
        Other("miscellaneous").print() shouldBe "miscellaneous"
    }
})
