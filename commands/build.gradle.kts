dependencies {
    api(project(":domain-editor"))
    api(project(":domain-tasks"))
    api(project(":plugin-api"))
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.kotest:kotest-property:5.7.2")
}

tasks.test {
    useJUnitPlatform()
}
