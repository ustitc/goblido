import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ContentTest : StringSpec({

    val content = Content.from(
        """
            Finish the PR +project
            Buy bread +home
            Have a coffee with Bryan @office
        """.trimIndent(),
    )

    "prints content" {
        content.print() shouldBe """
            Finish the PR +project
            Buy bread +home
            Have a coffee with Bryan @office
        """.trimIndent()
    }

    "changes text" {
        val result = content.changeText(
            """
                x Finish the PR +project
                Buy bread +home
            """.trimIndent()
        )

        result.print() shouldBe """
            x Finish the PR +project
            Buy bread +home
        """.trimIndent()
    }

    "changes text only of visible lines" {
        val withHiddenLines = content.hide(
            listOf(LineNumber(1)),
        )
        val result = withHiddenLines.changeText(
            """
                x Finish the PR +project
                Buy flowers
            """.trimIndent()
        )

        result.print() shouldBe """
            x Finish the PR +project
            Buy flowers
        """.trimIndent()
        result.lines shouldBe listOf(
            Line("x Finish the PR +project"),
            Line("Buy bread +home"),
            Line("Buy flowers"),
        )
    }

    "adds lines if new text has more lines than currently visible" {
        val withHiddenLines = content.hide(
            listOf(LineNumber(1)),
        )
        val result = withHiddenLines.changeText(
            """
                x Finish the PR +project
                Buy flowers
                Prepare an encounter +dnd
            """.trimIndent()
        )

        result.print() shouldBe """
            x Finish the PR +project
            Buy flowers
            Prepare an encounter +dnd
        """.trimIndent()
        result.lines shouldBe listOf(
            Line("x Finish the PR +project"),
            Line("Buy bread +home"),
            Line("Buy flowers"),
            Line("Prepare an encounter +dnd"),
        )
    }

    "adds text at the beginning of content if everything is not visible" {
        val withHiddenLines = content.hide(
            listOf(LineNumber(0), LineNumber(1), LineNumber(2)),
        )
        val result = withHiddenLines.changeText(
            """
                Buy Baldur's Gate
                Feed the cat
            """.trimIndent()
        )

        result.print() shouldBe """
            Buy Baldur's Gate
            Feed the cat
        """.trimIndent()
        result.lines shouldBe listOf(
            Line("Buy Baldur's Gate"),
            Line("Feed the cat"),
            Line("Finish the PR +project"),
            Line("Buy bread +home"),
            Line("Have a coffee with Bryan @office"),
        )
    }

    "removes lines if new text has less lines than currently visible" {
        val withHiddenLines = content.hide(
            listOf(LineNumber(1)),
        )
        val result = withHiddenLines.changeText(
            """
                x Finish the PR +project
            """.trimIndent()
        )

        result.print() shouldBe """
            x Finish the PR +project
        """.trimIndent()
        result.lines shouldBe listOf(
            Line("x Finish the PR +project"),
            Line("Buy bread +home"),
        )
    }

    "changes specific line" {
        val result = content.changeLine(
            lineNumber = LineNumber(2),
            line = Line("Have a beer with Bryan"),
        )

        result.print() shouldBe """
            Finish the PR +project
            Buy bread +home
            Have a beer with Bryan
        """.trimIndent()
    }

    "hides lines" {
        val result = content.hide(
            listOf(LineNumber(1), LineNumber(2))
        )

        result.print() shouldBe """
            Finish the PR +project
        """.trimIndent()
    }

    "hides only visible content" {
        val withHiddenLines = content.hide(
            listOf(LineNumber(0))
        )
        val result = withHiddenLines.hide(
            listOf(LineNumber(0))
        )

        result.print() shouldBe """
            Have a coffee with Bryan @office
        """.trimIndent()
    }

    "shows all content" {
        val hiddenContent = content.hide(
            listOf(LineNumber(1), LineNumber(2))
        )
        val result = hiddenContent.showAll()

        result.print() shouldBe """
            Finish the PR +project
            Buy bread +home
            Have a coffee with Bryan @office
        """.trimIndent()
    }

    "fails if task out-of-bound" {
        shouldThrow<IndexOutOfBoundsException> {
            content.changeLine(
                lineNumber = LineNumber(3),
                line = Line("Have a beer with Bryan"),
            )
        }
    }

    "get a line by number" {
        val lineNumber = LineNumber(2)
        val line = content[lineNumber]

        line shouldBe Line(text = "Have a coffee with Bryan @office")
    }

    "fails to get a line if line is out of bound" {
        shouldThrow<IndexOutOfBoundsException> {
            content[LineNumber(5)]
        }
    }
})
