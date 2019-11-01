/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

plugins {
    id("edu.wpi.first.GradleRIO")
}

dependencies {
    compile(project(":core"))
    compile("com.github.salomonbrys.kotson", "kotson", "2.5.0")
}
