/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.swerve

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Acceleration
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.inches
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitLengthModel
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitRotationModel
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnits

class SwerveModuleConstants {
    var kName = "Name"
    var kDriveTalonId = -1
    var kAzimuthTalonId = -1
    var kCanCoderId = -1

    var kCanCoderNativeUnitModel = NativeUnitRotationModel(2048.nativeUnits)

    // general azimuth
    var kInvertAzimuth = false
    var kInvertAzimuthSensorPhase = false
    var kAzimuthBrakeMode = true // neutral mode could change

    //        var kAzimuthTicksPerRadian = 4096.0 / (2 * Math.PI) // for azimuth
    var kAzimuthNativeUnitModel = NativeUnitRotationModel(2048.nativeUnits)
    var kAzimuthEncoderHomeOffset = 0.0

    // azimuth motion
    var kAzimuthKp = 1.3
    var kAzimuthKi = 0.05
    var kAzimuthKd = 20.0
    var kAzimuthKf = 0.5421
    var kAzimuthIZone = 25.0
    var kAzimuthCruiseVelocity = SIUnit<Velocity<Radian>>(2.6) // 1698 native units
    var kAzimuthAcceleration = SIUnit<Acceleration<Radian>>(31.26) // 20379 Native Units | 12 * kAzimuthCruiseVelocity
    var kAzimuthClosedLoopAllowableError = 5

    // azimuth current/voltage
    var kAzimuthContinuousCurrentLimit = 30 // amps
    var kAzimuthPeakCurrentLimit = 60 // amps
    var kAzimuthPeakCurrentDuration = 200 // ms
    var kAzimuthEnableCurrentLimit = true
    var kAzimuthMaxVoltage = 10.0 // volts
    var kAzimuthVoltageMeasurementFilter = 8 // # of samples in rolling average

    // azimuth measurement
    var kAzimuthStatusFrame2UpdateRate = 10 // feedback for selected sensor, ms
    var kAzimuthStatusFrame10UpdateRate = 10 // motion magic, ms// dt for velocity measurements, ms
    var kAzimuthVelocityMeasurementWindow = 64 // # of samples in rolling average

    // general drive
    var kInvertDrive = true
    var kInvertDriveSensorPhase = false
    var kDriveBrakeMode = true // neutral mode could change
    var kWheelDiameter = 4.0 // Probably should tune for each individual wheel maybe
    var kDriveNativeUnitModel = NativeUnitLengthModel(4096.nativeUnits, kWheelDiameter.inches)
    var kDriveDeadband = 0.01

    // var kDriveMaxSpeed = 10.0
    var kDriveMaxSpeed = 0.0

    // drive current/voltage
    var kDriveContinuousCurrentLimit = 30 // amps
    var kDrivePeakCurrentLimit = 50 // amps
    var kDrivePeakCurrentDuration = 200 // ms
    var kDriveEnableCurrentLimit = true
    var kDriveMaxVoltage = 11.0 // volts
    var kDriveVoltageMeasurementFilter = 8 // # of samples in rolling average

    // drive measurement
    var kDriveStatusFrame2UpdateRate = 15 // feedback for selected sensor, ms
    var kDriveStatusFrame10UpdateRate = 200 // motion magic, ms// dt for velocity measurements, ms
    var kDriveVelocityMeasurementWindow = 64 // # of samples in rolling average
}
