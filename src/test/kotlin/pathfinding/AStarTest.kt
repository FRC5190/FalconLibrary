import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Rotation2d
import frc.team5190.lib.math.trajectory.AStarOptimizer
import frc.team5190.lib.math.trajectory.Rectangle

fun main(args: Array<String>) {
    PathWindow

    val aStarTest = AStarOptimizer(
            PathWindow.ROBOT_SIZE,
            PathWindow.LEFT_SWITCH, PathWindow.PLATFORM, PathWindow.RIGHT_SWITCH
    )

    val result = aStarTest.optimize(
            Pose2d(PathWindow.LEFT_SWITCH.x - PathWindow.ROBOT_SIZE / 2.0, PathWindow.LEFT_SWITCH.y + PathWindow.ROBOT_SIZE, Rotation2d.fromDegrees(180.0)),
            Pose2d(23.7, 20.2, Rotation2d.fromDegrees(-15.0)),
            Rectangle(0.0, PathWindow.LEFT_SWITCH.y + PathWindow.LEFT_SWITCH.h / 2.0,
                    PathWindow.LEFT_SWITCH.x, PathWindow.LEFT_SWITCH.h)
    )!!

    PathWindow.rawPath = result.pathNodes
    PathWindow.path = result.path

    //AStarTest.start = Point(0.0, 2.5)
    //AStarTest.goal = Point( PathWindow.FIELD_LENGTH - 23.7, 20.2)

    //val startAngle = 0.0
    //val endAngle = -165.0

    //PathWindow.bannedAreas += Rectangle(0.0, PathWindow.LEFT_SWITCH.y, PathWindow.LEFT_SWITCH.x, PathWindow.LEFT_SWITCH.h)

    /*
    PathWindow.bannedAreas += Rectangle(PathWindow.LEFT_SWITCH.x + PathWindow.LEFT_SWITCH.w,
            PathWindow.PLATFORM.y + PathWindow.PLATFORM.h,
            PathWindow.FIELD_LENGTH / 2.0 - PathWindow.LEFT_SWITCH.w - PathWindow.LEFT_SWITCH.x,
            PathWindow.FIELD_WIDTH - PathWindow.PLATFORM.y - PathWindow.PLATFORM.h)
*/

    //println(path)

}