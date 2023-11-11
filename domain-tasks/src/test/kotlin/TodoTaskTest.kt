import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TodoTaskTest : StringSpec({

    "returns part of task" {
        val task = TodoTask("(A) create a web page +goblido and +web @home https://dinf.io due:2023-09-10")

        Part.parts(task) shouldBe listOf(
            Priority("A"),
            PlainText(" create a web page "),
            Project("goblido"),
            PlainText(" and "),
            Project("web"),
            PlainText(" "),
            Context("home"),
            PlainText(" "),
            WebLink("https://dinf.io"),
            PlainText(" "),
            Special("due", "2023-09-10"),
        )
    }

    "parses projects" {
        val task =
            TodoTask("+project +проект +工程项目 +big-project +big_project +important!!! +project1 ++plusplus +plus+plus")

        Part.parts(task).filterIsInstance<Project>() shouldBe listOf(
            Project("project"),
            Project("проект"),
            Project("工程项目"),
            Project("big-project"),
            Project("big_project"),
            Project("important!!!"),
            Project("project1"),
            Project("+plusplus"),
            Project("plus+plus"),
        )
    }

    "doesn't parse projects" {
        val task = TodoTask("do logic on cmd+d")

        Part.parts(task).filterIsInstance<Project>() shouldBe emptyList()
    }

    "parses contexts" {
        val text = "@context"
        val task = TodoTask(text)

        Part.parts(task).filterIsInstance<Context>() shouldBe listOf(
            Context("context"),
        )
    }

    "parses contexts in other languages" {
        val text = "@контекст @上下文环境"
        val task = TodoTask(text)

        Part.parts(task).filterIsInstance<Context>() shouldBe listOf(
            Context("контекст"),
            Context("上下文环境"),
        )
    }

    "parses contexts with non text symbols" {
        val text = "@big-context @big_context @important!!! @context1 @@context @context@context"
        val task = TodoTask(text)

        Part.parts(task).filterIsInstance<Context>() shouldBe listOf(
            Context("big-context"),
            Context("big_context"),
            Context("important!!!"),
            Context("context1"),
            Context("@context"),
            Context("context@context"),
        )
    }

    "parses links" {
        val task = TodoTask("https://dinf.io http://dinf.io")

        Part.parts(task).filterIsInstance<WebLink>() shouldBe listOf(
            WebLink("https://dinf.io"),
            WebLink("http://dinf.io"),
        )
    }

    "parses specials" {
        val task = TodoTask("key:value k1:v1 k-1:v-1 k!:v! k:v:v")

        Part.parts(task).filterIsInstance<Special>() shouldBe listOf(
            Special("key", "value"),
            Special("k1", "v1"),
            Special("k-1", "v-1"),
            Special("k!", "v!"),
            Special("k", "v:v"),
        )
    }
})
