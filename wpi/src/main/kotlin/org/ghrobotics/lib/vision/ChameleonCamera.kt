/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.vision

import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.wpilibj.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.milli
import org.ghrobotics.lib.wrappers.networktables.FalconNetworkTable
import org.ghrobotics.lib.wrappers.networktables.get

/**
 * Represents a camera connected to a co-processor which is running
 * Chameleon Vision.
 */
class ChameleonCamera(cameraName: String) {
    // NetworkTable for the specific camera
    private val table: NetworkTable = FalconNetworkTable.getTable("chameleon-vision").getSubTable(cameraName)

    // Entries for the NetworkTable
    private val pitchEntry = table["pitch"]
    private val yawEntry = table["yaw"]
    private val pipelineEntry = table["pipeline"]
    private val latencyEntry = table["latency"]
    private val driverModeEntry = table["driver_mode"]
    private val isValidEntry = table["is_valid"]

    /**
     * Returns the vertical angle to the target.
     */
    val pitch: Rotation2d
        get() = Rotation2d.fromDegrees(pitchEntry.getDouble(0.0))

    /**
     * Returns the horizontal angle to the target.
     */
    val yaw: Rotation2d
        get() = Rotation2d.fromDegrees(-yawEntry.getDouble(0.0)) // Negating to make it CCW positive.

    /**
     * Represents the latency in the pipeline between the capture
     * and reception of data on the roboRIO.
     */
    val latency: SIUnit<Second>
        get() = latencyEntry.getDouble(0.0).milli.seconds

    /**
     * Returns whether a target exists and is valid.
     */
    val isValid: Boolean
        get() = isValidEntry.getBoolean(false)

    /**
     * Gets or sets the value of the pipeline. This can be used
     * to switch between different Vision pipelines.
     */
    var pipeline: Double
        get() = pipelineEntry.getDouble(0.0)
        set(value) {
            pipelineEntry.setDouble(value)
        }

    /**
     * Toggles "driver mode". In "driver mode", the camera stream will
     * not have any Vision processing artifacts displayed on it.
     */
    var driverMode: Boolean
        get() = driverModeEntry.getBoolean(false)
        set(value) {
            driverModeEntry.setBoolean(value)
        }
}
