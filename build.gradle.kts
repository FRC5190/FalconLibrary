import edu.wpi.first.gradlerio.wpi.dependencies.WPIVendorDepsExtension
import edu.wpi.first.toolchain.NativePlatforms
import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("jvm") version "1.3.11"
    id("edu.wpi.first.GradleRIO") version "2019.1.1-beta-4b"
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
    compile("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.1.0")

    // WPILib
    wpi.deps.wpilib().forEach { compile(it) }
    wpi.deps.vendor.java().forEach { compile(it) }
    wpi.deps.vendor.jni(NativePlatforms.roborio).forEach { nativeZip(it) }
    wpi.deps.vendor.jni(NativePlatforms.desktop).forEach { nativeDesktopZip(it) }

    // Apache Commons Math
    compile("org.apache.commons", "commons-math3", "3.6.1")

    // Unit Testing
    testCompile("org.knowm.xchart", "xchart", "3.2.2")
    testCompile("junit", "junit", "4.12")
}

publishing {
    publications.withType<MavenPublication> {
        from(components["java"])
        groupId = "org.ghrobotics"
        artifactId = "FalconLibrary"
        version = "2019.01.05"
    }
}
tasks.withType<Wrapper>().configureEach {
    gradleVersion = "5.0"
}