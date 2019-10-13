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
    compile("org.apache.commons", "commons-math3", "3.6.1")
    compile("com.github.FRCTeam4069:Keigen:1.4.0")
    wpi.deps.wpilib().forEach { compile(it) }
}
