import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    id("io.gitlab.arturbosch.detekt") version("1.23.3") apply true
}

group = "dev.ustits"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = URI.create("https://jitpack.io") }
    }

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
        detektPlugins("io.nlopez.compose.rules:detekt:0.3.2")
        detektPlugins("com.github.hbmartin:hbmartin-detekt-rules:0.1.1")
    }
}
