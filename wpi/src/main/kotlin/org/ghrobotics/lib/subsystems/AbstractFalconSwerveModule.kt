/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems

import edu.wpi.first.math.kinematics.SwerveModuleState
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.motors.FalconMotor

abstract class AbstractFalconSwerveModule {
    abstract var driveMotor: FalconMotor<Meter>
    abstract var turnMotor: FalconMotor<Radian>

    abstract fun setControls(speed: Double, azimuth: Rotation2d)

    abstract fun setState(state: SwerveModuleState, arbitraryFeedForward: SIUnit<Volt> = 0.0.volts)

    /**
     * Resets turnMotor encoders
     *
     * @param angle
     */
    abstract fun resetAngle(angle: SIUnit<Radian> = SIUnit(0.0))

    /**
     * Reset drive encoders
     *
     * @param position
     */
    abstract fun resetDriveEncoder(position: SIUnit<Meter> = SIUnit(0.0))

    /**
     * Resets encoders for drive and turn encoders
     *
     */
    abstract fun reset()

    abstract fun state(): SwerveModuleState

    abstract fun setNeutral()

    abstract val voltageOutput: SIUnit<Volt>

    abstract val drawnCurrent: SIUnit<Ampere>

    abstract val drivePosition: SIUnit<Meter>

    abstract val driveVelocity: SIUnit<Velocity<Meter>>

    abstract val anglePosition: SIUnit<Radian>
}
