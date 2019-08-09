/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.commands

internal object FalconSubsystemHandler {
    private val registeredSubsystems = arrayListOf<FalconSubsystem>()

    fun add(subsystem: FalconSubsystem) {
        registeredSubsystems.add(subsystem)
    }

    fun lateInit() = registeredSubsystems.forEach { it.lateInit() }
    fun autoReset() = registeredSubsystems.forEach { it.autoReset() }
    fun teleopReset() = registeredSubsystems.forEach { it.teleopReset() }
    fun setNeutral() = registeredSubsystems.forEach { it.setNeutral() }
}