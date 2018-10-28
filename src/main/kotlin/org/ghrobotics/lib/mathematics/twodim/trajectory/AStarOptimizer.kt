package org.ghrobotics.lib.mathematics.twodim.trajectory

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.radian
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class AStarOptimizer(
        robotSize: Double,
        vararg restrictedAreas: Rectangle2d
) {

    companion object {
        private const val pointsPerFoot: Int = 4
        private val minAngleError = Math.toRadians(15.0)

        private val neighborTemplate = createNeighborTemplate(2)

        private fun createNeighborTemplate(size: Int): Set<NeighborPoint> {
            val points = mutableSetOf<NeighborPoint>()
            val range = (-size)..size
            for (x in range) {
                for (y in range) {
                    if (x == 0 && y == 0) continue // skip middle point
                    val worth = Math.sqrt((x * x + y * y).toDouble())
                    points += NeighborPoint(IntPoint(x, y), worth)
                }
            }
            return points
        }

        private val FIELD_RECTANGLE = Rectangle2d(0.0, 0.0, 54.0, 27.0)
    }

    private val robotRectangle = Rectangle2d(
            -robotSize / 2.0,
            -robotSize / 2.0,
            robotSize,
            robotSize
    )

    private val restrictedAreas = restrictedAreas.toList()

    fun optimize(start: Pose2d, goal: Pose2d, vararg restrictedAreas: Rectangle2d): Result? {
        val points = optimizePoints(start.translation, goal.translation, *restrictedAreas) ?: return null
        val cleanedUpPoints = points.removeRedundantPoints()
                .removeNearPoints(4.0)
                .addFarPoints(13.0)
                .fixEndPoints(start.translation, goal.translation)

        return Result(cleanedUpPoints, cleanedUpPoints.toWayPoints(start, goal))
    }

    private fun List<Translation2d>.toWayPoints(start: Pose2d, goal: Pose2d): List<Pose2d> {
        val newList = mutableListOf<Pose2d>()
        val pointAngles = zipWithNext { one, two -> Math.atan2(two.yRaw - one.yRaw, two.xRaw - one.xRaw) }

        for ((index, point) in withIndex()) {
            if (index == 0 || index == size - 1) {
                val rotation = if (index == 0) start.rotation else goal.rotation
                newList += Pose2d(point, rotation)
                continue
            }

            val last2Angle = pointAngles.getOrNull(index - 2)
            val lastAngle = pointAngles[index - 1]
            val nextAngle = pointAngles[index]
            val next2Angle = pointAngles.getOrNull(index + 1)

            var finalAngle: Double? = null

            if (next2Angle != null && (next2Angle - nextAngle).absoluteValue < minAngleError) finalAngle = nextAngle
            if (finalAngle == null && last2Angle != null && (last2Angle - lastAngle).absoluteValue < minAngleError) finalAngle =
                    lastAngle

            if (finalAngle == null) {
                val lastPoint = get(index - 1)
                val nextPoint = get(index + 1)

                val lastPointDistance = lastPoint.distance(point)
                val nextPointDistance = nextPoint.distance(point)

                finalAngle = (lastAngle * lastPointDistance + nextAngle * nextPointDistance) /
                        (lastPointDistance + nextPointDistance)
            }

            newList += Pose2d(point, finalAngle.radian)
        }
        return newList
    }

    private fun List<Translation2d>.fixEndPoints(start: Translation2d, goal: Translation2d): List<Translation2d> {
        val newList = mutableListOf<Translation2d>()
        newList += subList(1, size - 1)
        newList.add(0, start)
        newList.add(goal)
        return newList
    }

    private fun List<Translation2d>.addFarPoints(farDistance: Double): List<Translation2d> {
        val newList = mutableListOf<Translation2d>()
        for ((one, two) in zipWithNext()) {
            val distance = one.distance(two)
            newList += one
            if (distance > farDistance) {
                val amount = Math.ceil(distance / farDistance).toInt()
                val deltaTranslation = (two - one) / (amount + 1)
                for (i in 1..amount) {
                    newList += one + deltaTranslation * i
                }
            }
        }
        newList += last()
        return newList
    }

    private fun List<Translation2d>.removeNearPoints(nearDistance: Double): List<Translation2d> {
        val newList = mutableListOf<Translation2d>()
        val distances = zipWithNext { one, two -> one.distance(two) }.toMutableList()
        for ((index, point) in withIndex()) {
            if (index == 0 || index == size - 1) {
                // Never remove end points
                newList += point
                continue
            }
            val nextDistance =
                    if (index == size - 2) Math.min(distances[index - 1], distances[index]) else distances[index - 1]
            if (nextDistance > nearDistance) {
                newList += point
            } else if (index + 1 < distances.size) {
                distances[index + 1] += nextDistance
            }
        }
        return newList
    }

    private fun List<Translation2d>.removeRedundantPoints(): List<Translation2d> {
        val newList = mutableListOf<Translation2d>()
        val pointAngles =
                zipWithNext { one, two -> Math.atan2(two.yRaw - one.yRaw, two.xRaw - one.xRaw) }.toMutableList()
        for ((index, point) in withIndex()) {
            if (index == 0 || index == size - 1) {
                // Never remove end points
                newList += point
                continue
            }
            val lastAngle = pointAngles[index - 1]
            val nextAngle = pointAngles[index]
            if ((lastAngle - nextAngle).absoluteValue > minAngleError) newList += point
            else {
                pointAngles[index] = (lastAngle + nextAngle) / 2.0
            }
        }
        return newList
    }

    private fun optimizePoints(
            start: Translation2d,
            goal: Translation2d,
            vararg restrictedAreas: Rectangle2d
    ): List<Translation2d>? {
        val effectiveRestrictedAreas = this.restrictedAreas + restrictedAreas

        val startPoint = IntPoint(start, pointsPerFoot)
        val goalPoint = IntPoint(goal, pointsPerFoot)

        val startNode = Node(startPoint)

        val nodeCache = Array((FIELD_RECTANGLE.wRaw * pointsPerFoot).toInt()) { x ->
            Array<Node?>(((FIELD_RECTANGLE.hRaw * pointsPerFoot).toInt())) { y -> Node(IntPoint(x, y)) }
        }

        val closedSet = mutableSetOf<Node>()
        val openSet = mutableSetOf(startNode)

        while (openSet.isNotEmpty()) {
            val currentNode = openSet.minBy { it.fScore }!!
            if (currentNode.point.distance(goalPoint) <= 2) {
                // Got to goal
                val reconstructedPath = mutableListOf<Translation2d>()
                var current: Node? = currentNode
                while (current != null) {
                    val currentPoint = current.point
                    reconstructedPath += Translation2d(
                            currentPoint.x.toDouble() / pointsPerFoot,
                            currentPoint.y.toDouble() / pointsPerFoot
                    )
                    current = current.cameFrom
                }
                return reconstructedPath.reversed()
            }

            openSet -= currentNode
            closedSet += currentNode

            val currentRobotRectangle = currentNode.point.toRobotRectangle()

            for (neighborPoint in neighborTemplate.map { it + currentNode.point }) {
                val translatedRobotRectangle = neighborPoint.point.toRobotRectangle()
                val neighborNode = nodeCache[neighborPoint.point.x][neighborPoint.point.y] ?: continue
                // Check if point is within the field and not in the restricted areas
                if (!FIELD_RECTANGLE.isWithin(translatedRobotRectangle)
                        || effectiveRestrictedAreas.any { it.isIn(translatedRobotRectangle) }
                ) {
                    closedSet += neighborNode
                    nodeCache[neighborPoint.point.x][neighborPoint.point.y] = null
                    continue
                }

                // Check if it collides with restricted areas
                val translation2d = Translation2d(
                        neighborPoint.original.x.toDouble() / pointsPerFoot,
                        neighborPoint.original.y.toDouble() / pointsPerFoot
                )
                if (effectiveRestrictedAreas.any { it.doesCollide(currentRobotRectangle, translation2d) }) continue

                if (closedSet.contains(neighborNode)) continue

                val tentativeGScore = currentNode.gScore + neighborPoint.worth

                if (!openSet.contains(neighborNode)) {
                    openSet += neighborNode // new node
                } else if (tentativeGScore >= neighborNode.gScore) {
                    continue // not a better path
                }

                neighborNode.cameFrom = currentNode
                neighborNode.gScore = tentativeGScore
                neighborNode.fScore = tentativeGScore + neighborPoint.point.distance(goalPoint)
            }
        }

        return null
    }

    private fun IntPoint.toRobotRectangle() = Rectangle2d(
            robotRectangle.xRaw + x.toDouble() / pointsPerFoot,
            robotRectangle.yRaw + y.toDouble() / pointsPerFoot,
            robotRectangle.wRaw,
            robotRectangle.hRaw
    )

    private class NeighborPoint(
            val point: IntPoint,
            val worth: Double,
            val original: IntPoint = point
    ) {
        operator fun plus(point: IntPoint) = NeighborPoint(this.point + point, worth, original)

        override fun hashCode() = point.hashCode()
        override fun equals(other: Any?): Boolean {
            if (other !is NeighborPoint) return false
            return other.point == point
        }
    }

    private class Node(
            val point: IntPoint,
            var cameFrom: Node? = null,
            var gScore: Double = 0.0
    ) {
        var fScore = Double.POSITIVE_INFINITY

        override fun hashCode() = point.hashCode()
        override fun equals(other: Any?): Boolean {
            if (other !is Node) return false
            return other.point == point
        }
    }

    class Result(
            val pathNodes: List<Translation2d>,
            val path: List<Pose2d>
    )

}

private data class IntPoint(
        val x: Int,
        val y: Int
) {
    constructor(translation2d: Translation2d, pointsPerFoot: Int) : this(
            (translation2d.xRaw * pointsPerFoot).roundToInt(),
            (translation2d.yRaw * pointsPerFoot).roundToInt()
    )

    fun distance(other: IntPoint): Double {
        val xa = x - other.x
        val ya = y - other.y
        return Math.sqrt((xa * xa + ya * ya).toDouble())
    }

    operator fun plus(other: IntPoint) = IntPoint(x + other.x, y + other.y)
}

