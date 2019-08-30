/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.Subsystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.utils.launchFrequency

open class FalconNotifierCommand(
    private val period: SIUnit<Second>,
    vararg requirements: Subsystem,
    private val block: suspend CoroutineScope.() -> Unit
) : CommandBase() {

    init {
        addRequirements(*requirements)
    }

    lateinit var job: Job

    override fun initialize() {
        job = GlobalScope.launchFrequency((1 / period.value).toInt(), block = block)
    }

    override fun end(interrupted: Boolean) {
        job.cancel()
    }
}