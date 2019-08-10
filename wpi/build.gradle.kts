plugins {
    id("edu.wpi.first.GradleRIO")
}

repositories {
//    maven { setUrl("https://maven.woke.engineer/") }
}

dependencies {
    compile(project(":core"))
    compile("com.github.salomonbrys.kotson", "kotson", "2.5.0")
    compile("com.github.Oblarg:command-rewrite-jitpack:fb950f049d")

    wpi.deps.wpilib().forEach { compile(it) }

//    wpi.deps.wpilibJni().forEach { nativeZip(it) }
//    wpi.deps.wpilibDesktopJni().forEach { nativeDesktopZip(it) }
//    wpi.deps.wpilibJars().forEach { compile(it) }
}
