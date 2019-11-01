/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

import io.gitlab.arturbosch.detekt.detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30" apply false
    id("edu.wpi.first.GradleRIO") version "2020.1.1-beta-2" apply false
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC13"
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
