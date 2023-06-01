/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.geometry

import edu.wpi.first.math.geometry.Translation2d
import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.max
import org.ghrobotics.lib.mathematics.min
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit

@Suppress("FunctionName")
fun Rectangle2d(
    one: Translation2d,
    two: Translation2d,
): Rectangle2d {
    val minX = min(one.x_u, two.x_u)
    val minY = min(one.y_u, two.y_u)
    val maxX = max(one.x_u, two.x_u)
    val maxY = max(one.y_u, two.y_u)
    return Rectangle2d(
        minX,
        minY,
        maxX - minX,
        maxY - minY,
    )
}

@Suppress("FunctionName", "UnsafeCallOnNullableType")
fun Rectangle2d(
    vararg pointsToInclude: Translation2d,
): Rectangle2d {
    val minX = pointsToInclude.minByOrNull { it.x }!!.x_u
    val minY = pointsToInclude.minByOrNull { it.y }!!.y_u
    val maxX = pointsToInclude.maxByOrNull { it.x }!!.x_u
    val maxY = pointsToInclude.maxByOrNull { it.y }!!.y_u
    return Rectangle2d(
        minX,
        minY,
        maxX - minX,
        maxY - minY,
    )
}

data class Rectangle2d constructor(
    val x: SIUnit<Meter>,
    val y: SIUnit<Meter>,
    val w: SIUnit<Meter>,
    val h: SIUnit<Meter>,
) {

    val topLeft = Translation2d(x, y + h)
    val topRight = Translation2d(x + w, y + h)
    val bottomLeft = Translation2d(x, y)
    val bottomRight = Translation2d(x + w, y)

    val center = Translation2d(x + w / 2.0, y + h / 2.0)

    val maxCorner = topRight
    val minCorner = bottomLeft

    fun isIn(r: Rectangle2d) =
        x < r.x + r.w && x + w > r.x && y < r.y + r.h && y + h > r.y

    fun isWithin(r: Rectangle2d) = r.x in x..(x + w - r.w) && r.y in y..(y + h - r.h)

    operator fun contains(p: Translation2d) = p.x_u in x..(x + w) && p.y_u in y..(y + h)

    @Suppress("ComplexMethod")
    fun doesCollide(rectangle: Rectangle2d, translation: Translation2d): Boolean {
        if (translation.x epsilonEquals 0.0 && translation.y epsilonEquals 0.0) return false
        // Check if its even in range
        val boxRect = Rectangle2d(
            rectangle.topLeft,
            rectangle.bottomRight,
            rectangle.topLeft + translation,
            rectangle.bottomRight + translation,
        )
        // println(boxRect)
        if (!boxRect.isIn(this)) return false
        // AABB collision
        // Calculate distances
        val xInvEntry: SIUnit<Meter>
        val xInvExit: SIUnit<Meter>
        val yInvEntry: SIUnit<Meter>
        val yInvExit: SIUnit<Meter>
        if (translation.x > 0.0) {
            xInvEntry = (x - (rectangle.x + rectangle.w))
            xInvExit = ((x + w) - rectangle.x)
        } else {
            xInvEntry = ((x + w) - rectangle.x)
            xInvExit = (x - (rectangle.x + rectangle.w))
        }
        if (translation.y > 0.0) {
            yInvEntry = (y - (rectangle.y + rectangle.h))
            yInvExit = ((y + h) - rectangle.y)
        } else {
            yInvEntry = ((y + h) - rectangle.y)
            yInvExit = (y - (rectangle.y + rectangle.h))
        }
        // Find time of collisions
        val xEntry: Double
        val xExit: Double
        val yEntry: Double
        val yExit: Double
        if (translation.x epsilonEquals 0.0) {
            xEntry = Double.NEGATIVE_INFINITY
            xExit = Double.POSITIVE_INFINITY
        } else {
            xEntry = (xInvEntry / translation.x).value
            xExit = (xInvExit / translation.x).value
        }
        if (translation.y epsilonEquals 0.0) {
            yEntry = Double.NEGATIVE_INFINITY
            yExit = Double.POSITIVE_INFINITY
        } else {
            yEntry = (yInvEntry / translation.y).value
            yExit = (yInvExit / translation.y).value
        }
        val entryTime = max(xEntry, yEntry)
        val exitTime = min(xExit, yExit)

        return entryTime <= exitTime && (xEntry >= 0.0 || yEntry >= 0.0) && (xEntry < 1.0 || yEntry < 1.0)
    }
}
