plugins {
    id("edu.wpi.first.GradleRIO")
}

repositories {
    maven { setUrl("https://maven.woke.engineer/") }
}

dependencies {
    compile(project(":core"))
    compile("com.github.salomonbrys.kotson", "kotson", "2.5.0")

    wpi.deps.wpilibJni().forEach { nativeZip(it) }
    wpi.deps.wpilibDesktopJni().forEach { nativeDesktopZip(it) }
    wpi.deps.wpilibJars().forEach { compile(it) }
}