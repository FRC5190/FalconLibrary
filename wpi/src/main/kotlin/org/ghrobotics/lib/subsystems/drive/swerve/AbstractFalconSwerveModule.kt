/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.swerve

import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.motors.AbstractFalconAbsoluteEncoder
import org.ghrobotics.lib.motors.AbstractFalconMotor

interface AbstractFalconSwerveModule<D : AbstractFalconMotor<Meter>, T : AbstractFalconMotor<Radian>> {
    val driveMotor: D
    val azimuthMotor: T

    val encoder: AbstractFalconAbsoluteEncoder<Radian>

    fun setState(state: SwerveModuleState, arbitraryFeedForward: SIUnit<Volt> = 0.0.volts)

    fun swervePosition(): SwerveModulePosition

    fun setNeutral()

    fun setAngle(angle: Double)

    fun setVoltage(voltage: Double)

    val voltageOutput: SIUnit<Volt>

    val drawnCurrent: SIUnit<Ampere>

    val drivePosition: SIUnit<Meter>

    val driveVelocity: SIUnit<Velocity<Meter>>

    val anglePosition: SIUnit<Radian>
}
