package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.meter
import kotlin.math.max
import kotlin.math.min

@Suppress("FunctionName")
fun Rectangle2d(
    one: Translation2d,
    two: Translation2d
): Rectangle2d {
    val minX = min(one._x, two._x)
    val minY = min(one._y, two._y)
    val maxX = max(one._x, two._x)
    val maxY = max(one._y, two._y)
    return Rectangle2d(
        minX, minY,
        maxX - minX, maxY - minY
    )
}

@Suppress("FunctionName")
fun Rectangle2d(
    vararg pointsToInclude: Translation2d
): Rectangle2d {
    val minX = pointsToInclude.minBy { it._x }!!._x
    val minY = pointsToInclude.minBy { it._y }!!._y
    val maxX = pointsToInclude.maxBy { it._x }!!._x
    val maxY = pointsToInclude.maxBy { it._y }!!._y
    return Rectangle2d(
        minX, minY,
        maxX - minX, maxY - minY
    )
}

data class Rectangle2d internal constructor(
    internal val _x: Double,
    internal val _y: Double,
    internal val _w: Double,
    internal val _h: Double
) {

    val x get() = _x.meter
    val y get() = _y.meter
    val w get() = _w.meter
    val h get() = _h.meter

    constructor(x: Length, y: Length, w: Length, h: Length) :
        this(x.value, y.value, w.value, h.value)

    val topLeft = Translation2d(_x, _y + _h)
    val topRight = Translation2d(_x + _w, _y + _h)
    val bottomLeft = Translation2d(_x, _y)
    val bottomRight = Translation2d(_x + _w, _y)

    val center = Translation2d(_x + _w / 2, _y + _h / 2)

    val maxCorner = topRight
    val minCorner = bottomLeft

    fun isIn(r: Rectangle2d) =
        _x < r._x + r._w && _x + _w > r._x && _y < r._y + r._h && _y + _h > r._y

    fun isWithin(r: Rectangle2d) = r._x in _x..(_x + _w - r._w) && r._y in _y..(_y + _h - r._h)

    operator fun contains(p: Translation2d) = p._x in _x..(_x + _w) && p._y in _y..(_y + _h)

    fun doesCollide(rectangle: Rectangle2d, translation: Translation2d): Boolean {
        if (translation._x epsilonEquals 0.0 && translation._y epsilonEquals 0.0) return false
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
        if (translation._x > 0.0) {
            xInvEntry = (_x - (rectangle._x + rectangle._w))
            xInvExit = ((_x + _w) - rectangle._x)
        } else {
            xInvEntry = ((_x + _w) - rectangle._x)
            xInvExit = (_x - (rectangle._x + rectangle._w))
        }
        if (translation._y > 0.0) {
            yInvEntry = (_y - (rectangle._y + rectangle._h))
            yInvExit = ((_y + _h) - rectangle._y)
        } else {
            yInvEntry = ((_y + _h) - rectangle._y)
            yInvExit = (_y - (rectangle._y + rectangle._h))
        }
        // Find time of collisions
        val xEntry: Double
        val xExit: Double
        val yEntry: Double
        val yExit: Double
        if (translation._x epsilonEquals 0.0) {
            xEntry = Double.NEGATIVE_INFINITY
            xExit = Double.POSITIVE_INFINITY
        } else {
            xEntry = xInvEntry / translation._x
            xExit = xInvExit / translation._x
        }
        if (translation._y epsilonEquals 0.0) {
            yEntry = Double.NEGATIVE_INFINITY
            yExit = Double.POSITIVE_INFINITY
        } else {
            yEntry = yInvEntry / translation._y
            yExit = yInvExit / translation._y
        }
        val entryTime = max(xEntry, yEntry)
        val exitTime = min(xExit, yExit)

        return entryTime <= exitTime && (xEntry >= 0.0 || yEntry >= 0.0) && (xEntry < 1.0 || yEntry < 1.0)
    }
}