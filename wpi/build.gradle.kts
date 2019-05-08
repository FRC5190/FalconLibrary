plugins {
    id("edu.wpi.first.GradleRIO")
}

dependencies {
    compile(project(":core"))
    // Gson
    compile("com.github.salomonbrys.kotson", "kotson", "2.5.0")

    wpi.deps.wpilib().forEach { compile(it) }
}