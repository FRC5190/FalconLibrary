/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.localization

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.seconds
import org.ghrobotics.lib.mathematics.units.unitlessValue
import org.ghrobotics.lib.utils.Source
import java.util.TreeMap

class TimePoseInterpolatableBuffer(
    private val historySpan: SIUnit<Second> = 1.0.seconds,
    private val timeSource: Source<SIUnit<Second>> = { Timer.getFPGATimestamp().seconds },
) {

    private val bufferMap = TreeMap<SIUnit<Second>, Pose2d>()

    operator fun set(time: SIUnit<Second>, value: Pose2d): Pose2d? {
        cleanUp()
        return bufferMap.put(time, value)
    }

    private fun cleanUp() {
        val currentTime = timeSource()

        while (bufferMap.isNotEmpty()) {
            val entry = bufferMap.lastEntry()
            if (currentTime - entry.key >= historySpan) {
                bufferMap.remove(entry.key)
            } else {
                return
            }
        }
    }

    fun clear() {
        bufferMap.clear()
    }

    operator fun get(time: SIUnit<Second>): Pose2d? {
        if (bufferMap.isEmpty()) return null

        bufferMap[time]?.let { return it }

        val topBound = bufferMap.ceilingEntry(time)
        val bottomBound = bufferMap.floorEntry(time)

        return when {
            topBound == null -> bottomBound?.value
            bottomBound == null -> topBound.value
            else -> bottomBound.value.interpolate(
                topBound.value,
                ((time - bottomBound.key) / (topBound.key - bottomBound.key)).unitlessValue,
            )
        }
    }
}
