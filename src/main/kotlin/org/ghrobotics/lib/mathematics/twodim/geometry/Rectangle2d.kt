package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.utils.safeRangeTo
import kotlin.math.max
import kotlin.math.min

data class Rectangle2d(
        val x: Double,
        val y: Double,
        val w: Double,
        val h: Double
) {

    val topLeft = Translation2d(x, y + h)
    val topRight = Translation2d(x + w, y + h)
    val bottomLeft = Translation2d(x, y)
    val bottomRight = Translation2d(x + w, y)

    val maxCorner = topRight
    val minCorner = bottomLeft

    constructor(xRange: ClosedFloatingPointRange<Double>,
                yRange: ClosedFloatingPointRange<Double>) : this(
            xRange.start,
            yRange.start,
            xRange.endInclusive - xRange.start,
            yRange.endInclusive - yRange.start
    )

    constructor(one: Translation2d, two: Translation2d) : this(
            one.x.safeRangeTo(two.x),
            one.y.safeRangeTo(two.y)
    )

    fun isIn(r: Rectangle2d) = x < r.x + r.w && x + w > r.x && y < r.y + r.h && y + h > r.y

    fun isWithin(r: Rectangle2d) = r.x in x..(x + w - r.w) && r.y in y..(y + h - r.h)

    fun isWithin(p: Translation2d) = p.x in x..(x + w) && p.y in y..(y + h)

    fun doesCollide(rectangle: Rectangle2d, translation: Translation2d): Boolean {
        if (translation.x == 0.0 && translation.y == 0.0) return false
        // Check if its even in range
        val boxRect = Rectangle2d(
                if (translation.x > 0) rectangle.x else rectangle.x + translation.x,
                if (translation.y > 0) rectangle.x else rectangle.x + translation.y,
                if (translation.x > 0) translation.x + rectangle.w else rectangle.w - translation.x,
                if (translation.y > 0) translation.y + rectangle.h else rectangle.h - translation.y
        )
        //println(boxRect)
        if (!boxRect.isIn(this)) return false
        // AABB collision
        // Calculate distances
        val xInvEntry: Double
        val xInvExit: Double
        val yInvEntry: Double
        val yInvExit: Double
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
        if (translation.x == 0.0) {
            xEntry = Double.NEGATIVE_INFINITY
            xExit = Double.POSITIVE_INFINITY
        } else {
            xEntry = xInvEntry / translation.x
            xExit = xInvExit / translation.x
        }
        if (translation.y == 0.0) {
            yEntry = Double.NEGATIVE_INFINITY
            yExit = Double.POSITIVE_INFINITY
        } else {
            yEntry = yInvEntry / translation.y
            yExit = yInvExit / translation.y
        }
        val entryTime = max(xEntry, yEntry)
        val exitTime = min(xExit, yExit)

        return entryTime <= exitTime && (xEntry >= 0.0 || yEntry >= 0.0) && (xEntry < 1.0 || yEntry < 1.0)
    }
}