/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.vision

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.wpi.first.networktables.NetworkTable
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.milli
import org.ghrobotics.lib.wrappers.networktables.FalconNetworkTable
import org.ghrobotics.lib.wrappers.networktables.get
import org.opencv.core.RotatedRect

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
    private val areaEntry = table["area"]
    private val poseListEntry = table["poseList"]
    private val altTargetEntry = table["aux_targets"]

    private val gson = Gson()

    /**
     * Returns the vertical angle to the best target.
     */
    val pitch: Rotation2d
        get() = Rotation2d.fromDegrees(pitchEntry.getDouble(0.0))

    /**
     * Returns the horizontal angle to the best target.
     */
    val yaw: Rotation2d
        get() = Rotation2d.fromDegrees(-yawEntry.getDouble(0.0)) // Negating to make it CCW positive.

    /**
     * The area of the best target as a percentage of the total area of the screen.
     */
    val area: Double
        get() = areaEntry.getDouble(0.0)

    /**
     * The poses of all the targets detected.
     */
    val targetPoses: List<Pose2d>
        get() = gson.fromJson(poseListEntry.getString("[]"))

    /**
     * The camera relative pose of the best target.
     */
    val pose: Pose2d?
        get() = targetPoses.firstOrNull()

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
     * Returns a list representing all the targets currently being tracked, as ordered by the selected sorting method.
     */
    val altTargetList: List<ChameleonTrackedTarget>
        get() = gson.fromJson(altTargetEntry.getString("[]"))

    /**
     * Returns the current best ChameleonTrackedTarget.
     */
    val bestTarget: ChameleonTrackedTarget?
        get() = altTargetList.firstOrNull()

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

    data class ChameleonTrackedTarget(
        val pitch: Double,
        val yaw: Double,
        val area: Double,
        val cameraRelativePose: Pose2d
    )
}
