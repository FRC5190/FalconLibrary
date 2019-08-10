import io.gitlab.arturbosch.detekt.detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30" apply false
    id("edu.wpi.first.GradleRIO") version "2019.4.1" apply false
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC13"
    id("java")
    maven
    `maven-publish`

}

subprojects {
    apply {
        plugin("kotlin")
        plugin("maven")
        plugin("maven-publish")
    }
    tasks {
        withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs += "-Xjvm-default=compatibility"
            }
        }
    }
    repositories {
        jcenter()
        maven("https://jitpack.io")
    }
    dependencies {
        "compile"(kotlin("stdlib"))
        "compile"("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.2.0")

        "testCompile"("org.knowm.xchart", "xchart", "3.2.2")
        "testCompile"("junit", "junit", "4.12")
    }
}


detekt {
    config = files("$rootDir/detekt-config.yml")
    println(rootDir)

    reports {
        html {
            enabled = true
            destination = file("$rootDir/detekt.html")
        }
    }
}

tasks {
    withType<Wrapper>().configureEach {
        gradleVersion = "5.0"
    }
}
