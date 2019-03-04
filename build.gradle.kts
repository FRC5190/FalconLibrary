import edu.wpi.first.toolchain.NativePlatforms
import org.gradle.api.publish.maven.MavenPublication
import io.gitlab.arturbosch.detekt.detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
    id("edu.wpi.first.GradleRIO") version "2019.4.1"
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC13"
    maven
    `maven-publish`
}

repositories {
    jcenter()
    maven { setUrl("http://dl.bintray.com/kyonifer/maven") }
}

dependencies {
    // Kotlin Standard Library and Coroutines
    compile(kotlin("stdlib"))
    compile("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.1.1")

    // WPILib
    wpi.deps.wpilib().forEach { compile(it) }
    wpi.deps.vendor.java().forEach { compile(it) }
    wpi.deps.vendor.jni(NativePlatforms.roborio).forEach { nativeZip(it) }
    wpi.deps.vendor.jni(NativePlatforms.desktop).forEach { nativeDesktopZip(it) }

    // Apache Commons Math
    compile("org.apache.commons", "commons-math3", "3.6.1")

    // Gson
    compile("com.github.salomonbrys.kotson", "kotson", "2.5.0")

    // Unit Testing
    testCompile("org.knowm.xchart", "xchart", "3.2.2")
    testCompile("junit", "junit", "4.12")
}

publishing {
    publications {
        create<MavenPublication>("mavenLocal") {
            groupId = "org.ghrobotics"
            artifactId = "FalconLibrary"
            version = "2019.2.23"

            from(components["java"])
        }
    }
}

detekt {
    config = files("$projectDir/detekt-config.yml")

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
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs += "-Xjvm-default=compatibility"
        }
    }
}

