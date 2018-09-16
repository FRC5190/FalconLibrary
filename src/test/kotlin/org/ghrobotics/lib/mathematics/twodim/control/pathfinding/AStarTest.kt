import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.AStarOptimizer
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d

fun main(args: Array<String>) {
    PathWindow

    val aStarTest = AStarOptimizer(
            PathWindow.ROBOT_SIZE,
            PathWindow.LEFT_SWITCH, PathWindow.PLATFORM, PathWindow.RIGHT_SWITCH, PathWindow.BACK_SWITCH_CUBES
    )


    // ROBOT DIMENSIONS
    val kRobotWidth   = 27.0 / 12.0
    val kRobotLength  = 33.0 / 12.0
    val kIntakeLength = 16.0 / 12.0
    val kBumperLength = 02.0 / 12.0

    // ROBOT POSES
    val kRobotStartX = (kRobotLength / 2.0) + kBumperLength

    val kExchangeZoneBottomY = 14.5
    val kPortalZoneBottomY = 27 - (29.69 / 12.0)
    val kRobotSideStartY = kPortalZoneBottomY - (kRobotWidth / 2.0) - kBumperLength
    val kRobotCenterStartY = kExchangeZoneBottomY - (kRobotWidth / 2.0) - kBumperLength


    val result = aStarTest.optimize(
            Pose2d(Translation2d(kRobotStartX, kRobotSideStartY), Rotation2d()),
            Pose2d(Translation2d(23.700,6.800), Rotation2d.fromDegrees(20.0)),
            Rectangle2d(Translation2d(0.0, 0.0), Translation2d(10.0, 10.0))
    )!!

    PathWindow.rawPath = result.pathNodes
    PathWindow.path = result.path

    //AStarTest.start = Point(0.0, 2.5)
    //AStarTest.goal = Point( PathWindow.FIELD_LENGTH - 23.7, 20.2)

    //val startAngle = 0.0
    //val endAngle = -165.0

    //PathWindow.bannedAreas += Rectangle2d(0.0, PathWindow.LEFT_SWITCH.y, PathWindow.LEFT_SWITCH.x, PathWindow.LEFT_SWITCH.h)

    /*
    PathWindow.bannedAreas += Rectangle2d(PathWindow.LEFT_SWITCH.x + PathWindow.LEFT_SWITCH.w,
            PathWindow.PLATFORM.y + PathWindow.PLATFORM.h,
            PathWindow.FIELD_LENGTH / 2.0 - PathWindow.LEFT_SWITCH.w - PathWindow.LEFT_SWITCH.x,
            PathWindow.FIELD_WIDTH - PathWindow.PLATFORM.y - PathWindow.PLATFORM.h)
*/

    //println(path)

}