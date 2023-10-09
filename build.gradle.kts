import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "dev.ustits"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
    }
}

allprojects {
    apply(plugin = "kotlin")

    tasks.withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "17"
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }
}