import PathWindow.FIELD_WIDTH
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.DefaultTrajectoryGenerator
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.CentripetalAccelerationConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.second
import java.awt.*
import javax.swing.JFrame
import javax.swing.JPanel

object PathWindow {

    const val FIELD_WIDTH = 27.0
    const val FIELD_LENGTH = 54.0

    val FIELD = Rectangle2d(0.0, 0.0, FIELD_LENGTH, FIELD_WIDTH)

    val ROBOT_SIZE = 3.2 // 2.75

    val LEFT_SWITCH = Rectangle2d(140.0 / 12.0, 85.25 / 12.0, 56.0 / 12.0, 153.5 / 12.0)
    val PLATFORM = Rectangle2d(261.47 / 12.0, 95.25 / 12.0, 125.06 / 12.0, 133.5 / 12.0)
    val RIGHT_SWITCH = Rectangle2d(
        FIELD_LENGTH - (LEFT_SWITCH.xRaw + LEFT_SWITCH.wRaw),
        LEFT_SWITCH.yRaw,
        LEFT_SWITCH.wRaw,
        LEFT_SWITCH.hRaw
    )
    val BACK_SWITCH_CUBES =
        Rectangle2d(LEFT_SWITCH.xRaw + LEFT_SWITCH.wRaw, LEFT_SWITCH.yRaw, 15.0 / 12.0, LEFT_SWITCH.hRaw)

    val changeSync = Any()

    var bannedAreas = listOf<Rectangle2d>()
        set(value) = synchronized(changeSync) {
            field = value
            panel.repaint()
        }

    private var trajectory: TimedTrajectory<Pose2dWithCurvature>? = null

    var rawPath: List<Translation2d> = listOf()
        set(value) = synchronized(changeSync) {
            field = value
            panel.repaint()
        }

    var path: List<Pose2d> = listOf()
        set(value) = synchronized(changeSync) {
            println(value.joinToString("\n"))
            field = value
            panel.repaint()
            trajectory = DefaultTrajectoryGenerator.generateTrajectory(
                value,
                arrayListOf(CentripetalAccelerationConstraint(4.0)),
                0.feet.velocity,
                0.0.feet.velocity,
                10.0.feet.velocity,
                4.0.feet.acceleration,
                false
            )
        }

    private val frame = JFrame()
    private val panel = object : JPanel() {
        override fun paintComponent(g: Graphics) {
            g as Graphics2D
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            )

            g.background = Color.white
            g.color = Color.LIGHT_GRAY
            g.fillRect(PLATFORM)
            g.color = Color.GRAY
            g.fillRect(LEFT_SWITCH)
            g.fillRect(RIGHT_SWITCH)
            g.color = Color.YELLOW
            g.fillRect(BACK_SWITCH_CUBES)
            synchronized(changeSync) {
                g.color = Color(255, 0, 0, 64)
                for (bannedArea in bannedAreas) {
                    g.fillRect(bannedArea)
                }
                if (trajectory != null) {

                    g.color = Color.RED
                    g.stroke = BasicStroke(7.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f)
                    val trajectoryIterator = trajectory!!.iterator()

                    val points = mutableListOf<Translation2d>()

                    while (!trajectoryIterator.isDone) {
                        val point = trajectoryIterator.advance(0.02.second)
                        points += point.state.state.pose.translation
                    }

                    val scale = g.clipBounds.height / FIELD_WIDTH

                    var xPoints = IntArray(points.size) { (points[it].x.feet.asDouble * scale).toInt() }
                    var yPoints = IntArray(points.size) { (points[it].y.feet.asDouble * scale).toInt() }

                    g.drawPolyline(xPoints, yPoints, points.size)

                    g.color = Color.BLUE
                    g.stroke = BasicStroke(
                        3.0f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        10.0f,
                        arrayOf(10.0f).toFloatArray(),
                        0.0f
                    )

                    xPoints = IntArray(rawPath.size) { (rawPath[it].x.feet.asDouble * scale).toInt() }
                    yPoints = IntArray(rawPath.size) { (rawPath[it].y.feet.asDouble * scale).toInt() }

                    g.drawPolyline(xPoints, yPoints, rawPath.size)
                }
            }
        }
    }

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane = panel
        val size = 600
        frame.setSize((size / FIELD_WIDTH * FIELD_LENGTH).toInt(), size)
        frame.isResizable = false
        frame.isVisible = true
    }

}

private fun Graphics2D.drawLine(from: Translation2d, to: Translation2d) {
    val scale = clipBounds.height / FIELD_WIDTH
    drawLine((from.x.feet.asDouble * scale).toInt(), (from.y.feet.asDouble * scale).toInt(), (to.x.feet.asDouble * scale).toInt(), (to.y.feet.asDouble * scale).toInt())
}

private fun Graphics2D.fillRect(rectangle: Rectangle2d) {
    val scale = clipBounds.height / FIELD_WIDTH
    fillRect(
        (rectangle.xRaw * scale).toInt(),
        (rectangle.yRaw * scale).toInt(),
        (rectangle.wRaw * scale).toInt(),
        (rectangle.hRaw * scale).toInt()
    )
}
