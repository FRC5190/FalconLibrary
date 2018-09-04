import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Rotation2d
import org.junit.Test

class PathFinderTest {

    @Test
    fun testPathFinder(){
        PathWindow

        //AStarTest.start = Point(0.0, 2.5)
        AStarTest.start = Point(PathWindow.LEFT_SWITCH.x - PathWindow.ROBOT_SIZE / 2.0, PathWindow.LEFT_SWITCH.y + PathWindow.ROBOT_SIZE)
        AStarTest.goal = Point(23.7, 20.2)
        //AStarTest.goal = Point( PathWindow.FIELD_LENGTH - 23.7, 20.2)

        //val startAngle = 0.0
        val startAngle = 180.0
        val endAngle = -15.0
        //val endAngle = -165.0

        //PathWindow.bannedAreas += Rectangle(0.0, PathWindow.LEFT_SWITCH.y, PathWindow.LEFT_SWITCH.x, PathWindow.LEFT_SWITCH.h)
        PathWindow.bannedAreas += Rectangle(0.0, PathWindow.LEFT_SWITCH.y + PathWindow.LEFT_SWITCH.h / 2.0,
                PathWindow.LEFT_SWITCH.x, PathWindow.LEFT_SWITCH.h)

        /*
        PathWindow.bannedAreas += Rectangle(PathWindow.LEFT_SWITCH.x + PathWindow.LEFT_SWITCH.w,
                PathWindow.PLATFORM.y + PathWindow.PLATFORM.h,
                PathWindow.FIELD_LENGTH / 2.0 - PathWindow.LEFT_SWITCH.w - PathWindow.LEFT_SWITCH.x,
                PathWindow.FIELD_WIDTH - PathWindow.PLATFORM.y - PathWindow.PLATFORM.h)
    */

        val path = AStarTest.process()
                .optimize()
                .optimizeMinDistance(3.0)
                .optimizeMaxDistance(10.0)
        //println(path)

        PathWindow.astarPath = path

        val angles = path.zipWithNext { one, two -> Math.atan2(two.y - one.y, two.x - one.x) }
        val poseList = mutableListOf<Pose2d>()

        for ((index, point) in path.withIndex()) {
            if (index == 0) {
                poseList += Pose2d(point.x, point.y, Rotation2d.fromDegrees(startAngle))
                continue
            }
            if (index == path.size - 1) {
                poseList += Pose2d(point.x, point.y, Rotation2d.fromDegrees(endAngle))
                continue
            }
            val lastAngle = angles[index - 1]
            val nextAngle = angles[index]

            var finalAngle: Double? = null

            if(index > 1) {
                val last2Angle = angles[index - 2]
                if(lastAngle == last2Angle) finalAngle = last2Angle
            }

            if(index < path.size - 2) {
                val next2Angle = angles[index + 1]
                if(nextAngle == next2Angle) finalAngle = next2Angle
            }

            poseList += Pose2d(point.x, point.y, Rotation2d.fromRadians(finalAngle ?: (lastAngle * 0.5 + nextAngle) / 1.5))
        }

        PathWindow.path = poseList
    }

}

object AStarTest {

    var start = Point(0.0, 0.0)
        set(value) {
            field = fixToStepSize(value)
        }
    var goal = Point(0.0, 0.0)
        set(value) {
            field = fixToStepSize(value)
        }

    var stepSize = 0.5

    fun fixToStepSize(point: Point): Point{
        val factor = 1.0 / stepSize
        val x = (point.x * factor).toInt()
        val y = (point.y * factor).toInt()
        return Point(x / factor, y / factor)
    }

    fun process(): List<Point> {
        val startNode = Node(start)
        val nodeCache = mutableSetOf(startNode)

        val closedSet = mutableSetOf<Node>()
        val openSet = mutableSetOf(startNode)

        while (openSet.isNotEmpty()) {
            val current = openSet.minBy { it.fScore }!!
            if (current.point.distance(goal) <= stepSize) return createPath(current)

            openSet -= current
            closedSet += current

            for (neighbor in createNeighbors(nodeCache, current.point)) {
                if (closedSet.contains(neighbor)) continue

                val tentativeGScore = current.gScore + 1

                if (!openSet.contains(neighbor)) {
                    //println(neighbor.point)
                    openSet += neighbor // New node
                } else if (tentativeGScore >= neighbor.gScore) {
                    continue // Not a better path
                }

                neighbor.cameFrom = current
                neighbor.gScore = tentativeGScore
            }
        }

        return listOf()
    }

    private fun createNeighbors(nodeCache: MutableSet<Node>, point: Point) = listOf(
            point + Point(stepSize, 0.0),
            point - Point(stepSize, 0.0),
            point + Point(0.0, stepSize),
            point - Point(0.0, stepSize),
            point + Point(stepSize, stepSize),
            point - Point(stepSize, stepSize)
    ).filter { PathWindow.validSpot(it) }
            .map { neighborPoint ->
                val node = nodeCache.find { node -> node.point == neighborPoint } ?: Node(neighborPoint)
                nodeCache += node
                node
            }

    private fun createPath(node: Node): List<Point> {
        println("creating path")
        val path = mutableListOf<Point>()
        var current: Node? = node
        while (current != null) {
            path += current.point
            current = current.cameFrom
        }
        return path.reversed()
    }

    class Node(val point: Point,
               var cameFrom: Node? = null,
               var gScore: Double = Double.POSITIVE_INFINITY) {
        val fScore
            get() = gScore + point.distance(goal)

        override fun hashCode() = point.hashCode()
        override fun equals(other: Any?): Boolean {
            if (other !is Node) return false
            return other.point == point
        }
    }
}