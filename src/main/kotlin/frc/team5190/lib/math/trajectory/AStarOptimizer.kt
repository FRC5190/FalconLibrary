package frc.team5190.lib.math.trajectory

import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Rotation2d
import frc.team5190.lib.math.geometry.Translation2d

class AStarOptimizer(
        robotSize: Double,
        vararg restrictedAreas: Rectangle
) {

    companion object {
        private const val pointsPerFoot: Int = 1

        private val neighborTemplate = setOf(
                IntPoint(1, 0),
                IntPoint(-1, 0),
                IntPoint(0, 1),
                IntPoint(0, -1),
                IntPoint(1, 1),
                IntPoint(-1, 1),
                IntPoint(1, -1),
                IntPoint(-1, -1)
        )

        private val FIELD_RECTANGLE = Rectangle(0.0, 0.0, 54.0, 27.0)
    }

    private val robotRectangle = Rectangle(
            -robotSize / 2.0,
            -robotSize / 2.0,
            robotSize,
            robotSize
    )

    private val restrictedAreas = restrictedAreas.toList()

    fun optimize(start: Pose2d, goal: Pose2d, vararg restrictedAreas: Rectangle): Result? {
        val points = optimizePoints(start.translation, goal.translation, *restrictedAreas) ?: return null
        val cleanedUpPoints = points.removeRedundantPoints()
                .removeNearPoints(3.0)
                .addFarPoints(10.0)
                .fixEndPoints(start.translation, goal.translation)

        return Result(cleanedUpPoints, cleanedUpPoints.toWayPoints(start, goal))
    }

    private fun List<Translation2d>.toWayPoints(start: Pose2d, goal: Pose2d): List<Pose2d> {
        val newList = mutableListOf<Pose2d>()
        val pointAngles = zipWithNext { one, two -> Math.atan2(two.y - one.y, two.x - one.x) }

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

            if (next2Angle == nextAngle) finalAngle = nextAngle
            if (finalAngle == null && last2Angle == lastAngle) finalAngle = lastAngle

            if (finalAngle == null) finalAngle = (lastAngle * 0.5 + nextAngle) / 1.5

            newList += Pose2d(point, Rotation2d.fromRadians(finalAngle))
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
            if (distance > farDistance) newList += (one + two) / 2.0
        }
        newList += last()
        return newList
    }

    private fun List<Translation2d>.removeNearPoints(nearDistance: Double): List<Translation2d> {
        val newList = mutableListOf<Translation2d>()
        val distances = zipWithNext { one, two -> one.distance(two) }
        for ((index, point) in withIndex()) {
            if (index == 0 || index == size - 1) {
                // Never remove end points
                newList += point
                continue
            }
            val nextDistance = distances[index]
            if (nextDistance > nearDistance) newList += point
        }
        return newList
    }

    private fun List<Translation2d>.removeRedundantPoints(): List<Translation2d> {
        val newList = mutableListOf<Translation2d>()
        val pointAngles = zipWithNext { one, two -> Math.atan2(two.y - one.y, two.x - one.x) }
        for ((index, point) in withIndex()) {
            if (index == 0 || index == size - 1) {
                // Never remove end points
                newList += point
                continue
            }
            val lastAngle = pointAngles[index - 1]
            val nextAngle = pointAngles[index]
            if (lastAngle != nextAngle) newList += point
        }
        return newList
    }

    private fun optimizePoints(start: Translation2d, goal: Translation2d, vararg restrictedAreas: Rectangle): List<Translation2d>? {
        val effectiveRestrictedAreas = this.restrictedAreas + restrictedAreas

        val startPoint = IntPoint(start, pointsPerFoot)
        val goalPoint = IntPoint(goal, pointsPerFoot)

        val startNode = Node(startPoint)

        val nodeCache = mutableMapOf(startPoint to startNode)

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

            for (neighborPoint in neighborTemplate.map { currentNode.point + it }) {
                val translatedRobotRectangle = Rectangle(
                        robotRectangle.x + neighborPoint.x,
                        robotRectangle.y + neighborPoint.y,
                        robotRectangle.w,
                        robotRectangle.h
                )
                // Check if point is within the field and not in the restricted areas
                if (!FIELD_RECTANGLE.isIn(translatedRobotRectangle)
                        || effectiveRestrictedAreas.any { it.isIn(translatedRobotRectangle) }) continue

                val neighborNode = nodeCache.computeIfAbsent(neighborPoint) { Node(it) }

                if (closedSet.contains(neighborNode)) continue

                val tentativeGScore = currentNode.gScore + 1

                if (!openSet.contains(neighborNode)) {
                    openSet += neighborNode // new node
                } else if (tentativeGScore >= neighborNode.gScore) {
                    continue // not a better path
                }

                neighborNode.cameFrom = currentNode
                neighborNode.gScore = tentativeGScore
                neighborNode.fScore = tentativeGScore + neighborPoint.distance(goalPoint)
            }
        }

        return null
    }

    private class Node(
            val point: IntPoint,
            var cameFrom: Node? = null,
            var gScore: Double = Double.POSITIVE_INFINITY
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
            (translation2d.x * pointsPerFoot).toInt(),
            (translation2d.y * pointsPerFoot).toInt()
    )

    fun distance(other: IntPoint): Double {
        val xa = x - other.x
        val ya = y - other.y
        return Math.sqrt((xa * xa + ya * ya).toDouble())
    }

    operator fun plus(other: IntPoint) = IntPoint(x + other.x, y + other.y)
}

data class Rectangle(
        val x: Double,
        val y: Double,
        val w: Double,
        val h: Double
) {
    fun isIn(r: Rectangle) = x < r.x + r.w && x + w > r.x && y < r.y + r.h && y + h > r.y
}