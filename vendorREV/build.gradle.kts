plugins {
    id("edu.wpi.first.GradleRIO")
}

repositories {
//    maven { setUrl("https://maven.woke.engineer/") }
}


dependencies {
    compile(project(":core"))
    compile(project(":wpi"))
    wpi.deps.vendor.java().forEach { compile(it) }
}