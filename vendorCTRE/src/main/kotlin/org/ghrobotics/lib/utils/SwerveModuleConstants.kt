/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import com.ctre.phoenix.motorcontrol.NeutralMode

class SwerveModuleConstants() {
    var kName = "Name"
    var kDriveTalonId = -1
    var kAzimuthTalonId = -1

    // general azimuth
    var kInvertAzimuth = false
    var kInvertAzimuthSensorPhase = false
    var kAzimuthInitNeutralMode: NeutralMode = NeutralMode.Brake // neutral mode could change

    var kAzimuthEncoderHomeOffset = 0.0

    // azimuth motion
    var kAzimuthKp = 1.3
    var kAzimuthKi = 0.05
    var kAzimuthKd = 20.0
    var kAzimuthKf = 0.5421
    var kAzimuthIZone = 25
    var kAzimuthCruiseVelocity = 1698
    var kAzimuthAcceleration = 20379 // 12 * kAzimuthCruiseVelocity

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

    var kAzimuthStatusFrame10UpdateRate = 10 // motion magic, ms

    var kAzimuthVelocityMeasurementWindow = 64 // # of samples in rolling average

    // general drive
    var kInvertDrive = true
    var kInvertDriveSensorPhase = false
    var kDriveInitNeutralMode: NeutralMode = NeutralMode.Brake // neutral mode could change

    var kWheelDiameter = 4.0 // Probably should tune for each individual wheel maybe

    var kDriveTicksPerUnitDistance = (1.0 / 4096.0 * (18.0 / 28.0 * 15.0 / 45.0) *
        (Math.PI * kWheelDiameter))
    var kDriveDeadband = 0.01

    // drive current/voltage
    var kDriveContinuousCurrentLimit = 30 // amps

    var kDrivePeakCurrentLimit = 50 // amps

    var kDrivePeakCurrentDuration = 200 // ms

    var kDriveEnableCurrentLimit = true
    var kDriveMaxVoltage = 10.0 // volts

    var kDriveVoltageMeasurementFilter = 8 // # of samples in rolling average

    // drive measurement
}
