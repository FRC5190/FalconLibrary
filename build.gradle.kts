import edu.wpi.first.gradlerio.wpi.dependencies.WPIVendorDepsExtension
import edu.wpi.first.toolchain.NativePlatforms
import org.gradle.api.publish.maven.MavenPublication

plugins {
    kotlin("jvm") version "1.3.11"
    id("edu.wpi.first.GradleRIO") version "2019.1.1-beta-4"
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
    compile("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.0.1")

    // WPILib
    wpi.deps.wpilib().forEach { compile(it) }

    // wpi.deps.vendor.java().forEach { compile(it) }

    // This is the ugly workaround for the commented code that should work above.
    // Until https://github.com/wpilibsuite/GradleRIO/pull/267 gets merged, we will have to use this.

    wpi.deps.vendor.dependencies.map { dep ->
        dep.javaDependencies.map { art ->
            "${art.groupId}:${art.artifactId}:${WPIVendorDepsExtension.getVersion(art.version, wpi.deps.vendor.wpiExt)}"
        }
    }.forEach { it.forEach { compile(it) } }

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