package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.max
import org.ghrobotics.lib.mathematics.min
import org.ghrobotics.lib.mathematics.units.Length
import kotlin.math.max
import kotlin.math.min

@Suppress("FunctionName")
fun Rectangle2d(
    one: Translation2d,
    two: Translation2d
): Rectangle2d {
    val minX = min(one.x, two.x)
    val minY = min(one.y, two.y)
    val maxX = max(one.x, two.x)
    val maxY = max(one.y, two.y)
    return Rectangle2d(
        minX, minY,
        maxX - minX, maxY - minY
    )
}

fun Rectangle2d(
    vararg pointsToInclude: Translation2d
): Rectangle2d {
    val minX = pointsToInclude.minBy { it.x }!!.x
    val minY = pointsToInclude.minBy { it.y }!!.y
    val maxX = pointsToInclude.maxBy { it.x }!!.x
    val maxY = pointsToInclude.maxBy { it.y }!!.y
    return Rectangle2d(
        minX, minY,
        maxX - minX, maxY - minY
    )
}

data class Rectangle2d(
    val x: Length,
    val y: Length,
    val w: Length,
    val h: Length
) {

    val topLeft = Translation2d(x, y + h)
    val topRight = Translation2d(x + w, y + h)
    val bottomLeft = Translation2d(x, y)
    val bottomRight = Translation2d(x + w, y)

    val center = Translation2d(x + w / 2, y + h / 2)

    val maxCorner = topRight
    val minCorner = bottomLeft

    fun isIn(r: Rectangle2d) =
        x < r.x + r.w && x + w > r.x && y < r.y + r.h && y + h > r.y

    fun isWithin(r: Rectangle2d) = r.x in x..(x + w - r.w) && r.y in y..(y + h - r.h)

    operator fun contains(p: Translation2d) = p.x in x..(x + w) && p.y in y..(y + h)

    fun doesCollide(rectangle: Rectangle2d, translation: Translation2d): Boolean {
        if (translation.x.value epsilonEquals 0.0 && translation.y.value epsilonEquals 0.0) return false
        // Check if its even in range
        val boxRect = Rectangle2d(
            rectangle.topLeft, rectangle.bottomRight,
            rectangle.topLeft + translation, rectangle.bottomRight + translation
        )
        //println(boxRect)
        if (!boxRect.isIn(this)) return false
        // AABB collision
        // Calculate distances
        val xInvEntry: Double
        val xInvExit: Double
        val yInvEntry: Double
        val yInvExit: Double
        if (translation.x.value > 0.0) {
            xInvEntry = (x.value - (rectangle.x.value + rectangle.w.value))
            xInvExit = ((x.value + w.value) - rectangle.x.value)
        } else {
            xInvEntry = ((x.value + w.value) - rectangle.x.value)
            xInvExit = (x.value - (rectangle.x.value + rectangle.w.value))
        }
        if (translation.y.value > 0.0) {
            yInvEntry = (y.value - (rectangle.y.value + rectangle.h.value))
            yInvExit = ((y.value + h.value) - rectangle.y.value)
        } else {
            yInvEntry = ((y.value + h.value) - rectangle.y.value)
            yInvExit = (y.value - (rectangle.y.value + rectangle.h.value))
        }
        // Find time of collisions
        val xEntry: Double
        val xExit: Double
        val yEntry: Double
        val yExit: Double
        if (translation.x.value epsilonEquals 0.0) {
            xEntry = Double.NEGATIVE_INFINITY
            xExit = Double.POSITIVE_INFINITY
        } else {
            xEntry = xInvEntry / translation.x.value
            xExit = xInvExit / translation.x.value
        }
        if (translation.y.value epsilonEquals 0.0) {
            yEntry = Double.NEGATIVE_INFINITY
            yExit = Double.POSITIVE_INFINITY
        } else {
            yEntry = yInvEntry / translation.y.value
            yExit = yInvExit / translation.y.value
        }
        val entryTime = max(xEntry, yEntry)
        val exitTime = min(xExit, yExit)

        return entryTime <= exitTime && (xEntry >= 0.0 || yEntry >= 0.0) && (xEntry < 1.0 || yEntry < 1.0)
    }
}