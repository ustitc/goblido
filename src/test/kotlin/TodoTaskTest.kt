import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TodoTaskTest : StringSpec({

    "returns part of task" {
        val task = TodoTask("(A) create a web page +goblido and +web @home https://dinf.io")

        task.parts() shouldBe listOf(
            Priority("A"),
            PlainText(" create a web page "),
            Project("goblido"),
            PlainText(" and "),
            Project("web"),
            PlainText(" "),
            Context("home"),
            PlainText(" "),
            WebLink("https://dinf.io"))
    }

    "parses projects" {
        val task = TodoTask("+project +проект +工程项目 +big-project +big_project +important!!! +project1 ++plusplus +plus+plus")

        task.parts().filterIsInstance<Project>() shouldBe listOf(
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

        task.parts().filterIsInstance<Project>() shouldBe emptyList()
    }

    "parses contexts" {
        val task = TodoTask("@context @контекст @上下文环境 @big-context @big_context @important!!! @context1 @@context @context@context")

        task.parts().filterIsInstance<Context>() shouldBe listOf(
            Context("context"),
            Context("контекст"),
            Context("上下文环境"),
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

        task.parts().filterIsInstance<WebLink>() shouldBe listOf(
            WebLink("https://dinf.io"),
            WebLink("http://dinf.io"),
        )
    }

})
