/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.debug

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.wpi.first.wpilibj.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.derived.toRotation2d
import org.ghrobotics.lib.mathematics.units.meters
import org.ghrobotics.lib.wrappers.networktables.FalconNetworkTable
import org.ghrobotics.lib.wrappers.networktables.delegate
import org.ghrobotics.lib.wrappers.networktables.get

/**
 * Singleton Object representing the network table for the Live Dashboard
 */
object LiveDashboard {
    val liveDashboardTable = FalconNetworkTable.getTable("Live_Dashboard")

    var robotX by liveDashboardTable["robotX"].delegate(0.0)
    var robotY by liveDashboardTable["robotY"].delegate(0.0)
    var robotHeading by liveDashboardTable["robotHeading"].delegate(0.0)

    var isFollowingPath by liveDashboardTable["isFollowingPath"].delegate(false)
    var pathX by liveDashboardTable["pathX"].delegate(0.0)
    var pathY by liveDashboardTable["pathY"].delegate(0.0)
    var pathHeading by liveDashboardTable["pathHeading"].delegate(0.0)

    private val visionTargetEntry = liveDashboardTable["visionTargets"]
    var visionTargets: List<Pose2d>
        set(value) {
            visionTargetEntry.setStringArray(
                value.map {
                    jsonObject(
                        "x" to it.translation.x,
                        "y" to it.translation.y,
                        "angle" to it.rotation.degrees
                    ).toString()
                }.toTypedArray()
            )
        }
        get() = visionTargetEntry.getStringArray(emptyArray())
            .map {
                val data = kGson.fromJson<JsonObject>(it)
                Pose2d(
                    data["x"].asDouble.meters,
                    data["y"].asDouble.meters,
                    data["angle"].asDouble.degrees.toRotation2d()
                )
            }

    private val kGson = Gson()
}