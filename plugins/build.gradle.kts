dependencies {
    implementation(project(":plugin-api"))
    testImplementation(project(":domain"))
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.kotest:kotest-property:5.7.2")
}

tasks.test {
    useJUnitPlatform()
}
