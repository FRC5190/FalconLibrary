package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.utils.safeRangeTo
import kotlin.math.max
import kotlin.math.min

data class Rectangle2d(
    val xRaw: Double,
    val yRaw: Double,
    val wRaw: Double,
    val hRaw: Double
) {

    val topLeft = Translation2d(xRaw, yRaw + hRaw)
    val topRight = Translation2d(xRaw + wRaw, yRaw + hRaw)
    val bottomLeft = Translation2d(xRaw, yRaw)
    val bottomRight = Translation2d(xRaw + wRaw, yRaw)

    val maxCorner = topRight
    val minCorner = bottomLeft

    val x
        get() = xRaw.meter
    val y
        get() = yRaw.meter
    val w
        get() = wRaw.meter
    val h
        get() = hRaw.meter

    constructor(
        xRange: ClosedFloatingPointRange<Double>,
        yRange: ClosedFloatingPointRange<Double>
    ) : this(
            xRange.start,
            yRange.start,
            xRange.endInclusive - xRange.start,
            yRange.endInclusive - yRange.start
    )

    constructor(one: Translation2d, two: Translation2d) : this(
            one.xRaw.safeRangeTo(two.xRaw),
            one.yRaw.safeRangeTo(two.yRaw)
    )

    fun isIn(r: Rectangle2d) =
            xRaw < r.xRaw + r.wRaw && xRaw + wRaw > r.xRaw && yRaw < r.yRaw + r.hRaw && yRaw + hRaw > r.yRaw

    fun isWithin(r: Rectangle2d) = r.xRaw in xRaw..(xRaw + wRaw - r.wRaw) && r.yRaw in yRaw..(yRaw + hRaw - r.hRaw)

    operator fun contains(p: Translation2d) = p.xRaw in xRaw..(xRaw + wRaw) && p.yRaw in yRaw..(yRaw + hRaw)

    fun doesCollide(rectangle: Rectangle2d, translation: Translation2d): Boolean {
        if (translation.xRaw == 0.0 && translation.yRaw == 0.0) return false
        // Check if its even in range
        val boxRect = Rectangle2d(
                if (translation.xRaw > 0) rectangle.xRaw else rectangle.xRaw + translation.xRaw,
                if (translation.yRaw > 0) rectangle.xRaw else rectangle.xRaw + translation.yRaw,
                if (translation.xRaw > 0) translation.xRaw + rectangle.wRaw else rectangle.wRaw - translation.xRaw,
                if (translation.yRaw > 0) translation.yRaw + rectangle.hRaw else rectangle.hRaw - translation.yRaw
        )
        //println(boxRect)
        if (!boxRect.isIn(this)) return false
        // AABB collision
        // Calculate distances
        val xInvEntry: Double
        val xInvExit: Double
        val yInvEntry: Double
        val yInvExit: Double
        if (translation.xRaw > 0.0) {
            xInvEntry = (xRaw - (rectangle.xRaw + rectangle.wRaw))
            xInvExit = ((xRaw + wRaw) - rectangle.xRaw)
        } else {
            xInvEntry = ((xRaw + wRaw) - rectangle.xRaw)
            xInvExit = (xRaw - (rectangle.xRaw + rectangle.wRaw))
        }
        if (translation.yRaw > 0.0) {
            yInvEntry = (yRaw - (rectangle.yRaw + rectangle.hRaw))
            yInvExit = ((yRaw + hRaw) - rectangle.yRaw)
        } else {
            yInvEntry = ((yRaw + hRaw) - rectangle.yRaw)
            yInvExit = (yRaw - (rectangle.yRaw + rectangle.hRaw))
        }
        // Find time of collisions
        val xEntry: Double
        val xExit: Double
        val yEntry: Double
        val yExit: Double
        if (translation.xRaw == 0.0) {
            xEntry = Double.NEGATIVE_INFINITY
            xExit = Double.POSITIVE_INFINITY
        } else {
            xEntry = xInvEntry / translation.xRaw
            xExit = xInvExit / translation.xRaw
        }
        if (translation.yRaw == 0.0) {
            yEntry = Double.NEGATIVE_INFINITY
            yExit = Double.POSITIVE_INFINITY
        } else {
            yEntry = yInvEntry / translation.yRaw
            yExit = yInvExit / translation.yRaw
        }
        val entryTime = max(xEntry, yEntry)
        val exitTime = min(xExit, yExit)

        return entryTime <= exitTime && (xEntry >= 0.0 || yEntry >= 0.0) && (xEntry < 1.0 || yEntry < 1.0)
    }
}