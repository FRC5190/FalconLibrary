/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.utils

import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import java.text.DecimalFormat

/**
 * A drivetrain signal containing the speed and azimuth for each wheel
 */
class DriveSignal @JvmOverloads constructor(
    val wheelSpeeds: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0, 0.0),
    wheelAzimuths: Array<Rotation2d> = arrayOf<Rotation2d>(
        Rotation2d.identity(),
        Rotation2d.identity(),
        Rotation2d.identity(),
        Rotation2d.identity(),
    ),
) {
    private val mWheelAzimuths: Array<Rotation2d> // Radians!: Array<Rotation2d>

    init {
        mWheelAzimuths = wheelAzimuths
    }

    val wheelAzimuths: Array<Rotation2d>
        get() = mWheelAzimuths

    override fun toString(): String {
        var ret_val = "DriveSignal - \n"
        val fmt = DecimalFormat("#0.000")
        for (i in wheelSpeeds.indices) {
            ret_val += """	Wheel $i: Speed - ${wheelSpeeds[i]}, Azimuth - ${fmt.format(mWheelAzimuths[i].degrees)} deg
"""
        }
        return ret_val
    }
}
