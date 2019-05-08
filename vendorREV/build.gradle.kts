plugins {
    id("edu.wpi.first.GradleRIO")
}

dependencies {
    compile(project(":core"))
    compile(project(":wpi"))
    wpi.deps.vendor.java().forEach { compile(it) }
}