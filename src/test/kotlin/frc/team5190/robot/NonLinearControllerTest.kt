/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.robot

import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Pose2dWithCurvature
import frc.team5190.lib.math.geometry.Twist2d
import frc.team5190.lib.math.trajectory.Trajectory
import frc.team5190.lib.math.trajectory.TrajectoryIterator
import frc.team5190.lib.math.trajectory.followers.NonLinearController
import frc.team5190.lib.math.trajectory.followers.TrajectoryFollower
import frc.team5190.lib.math.trajectory.timing.TimedState
import frc.team5190.lib.math.trajectory.view.TimedView
import frc.team5190.robot.auto.FastTrajectories
import frc.team5190.robot.util.DriveConstants
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import java.awt.Color
import java.awt.Font
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class NonLinearControllerTest {

    private lateinit var trajectoryFollower: TrajectoryFollower

    @Test
    fun testTrajectoryFollower() {
        val name = "Center Start to Left Switch"
        val trajectory: Trajectory<TimedState<Pose2dWithCurvature>> = FastTrajectories.centerStartToLeftSwitch
        val iterator = TrajectoryIterator(TimedView(trajectory))
        trajectoryFollower = NonLinearController(trajectory, DriveConstants.FOLLOW_BETA, DriveConstants.FOLLOW_ZETA)

        var totalpose = trajectory.firstState.state.pose
        var prevdx = 0.0
        var prevdtheta = 0.0

        var time = 0.0
        val dt = 0.02

        val xList = arrayListOf<Double>()
        val yList = arrayListOf<Double>()

        val refXList = arrayListOf<Double>()
        val refYList = arrayListOf<Double>()

        while (!iterator.isDone) {
            val pt = iterator.advance(dt)
            val output = trajectoryFollower.getSteering(totalpose, time.toLong()).scaled(0.02)
            time += dt * 1.0e+9

            totalpose = totalpose.transformBy(Pose2d.fromTwist(Twist2d(
                    output.dx + 0.0 * prevdx,
                    output.dy,
                    output.dtheta + 0.0 * prevdtheta)))


            prevdx = output.dx
            prevdtheta = output.dtheta

            xList.add(totalpose.translation.x)
            yList.add(totalpose.translation.y)

            refXList.add(pt.state.state.translation.x)
            refYList.add(pt.state.state.translation.y)
        }

        val fm = DecimalFormat("#.###").format(trajectory.lastState.t)

        val chart = XYChartBuilder().width(1800).height(1520).title("$name: $fm seconds.")
                .xAxisTitle("X").yAxisTitle("Y").build()

        chart.styler.markerSize = 8
        chart.styler.seriesColors = arrayOf(Color.ORANGE, Color(151, 60, 67))

        chart.styler.chartTitleFont = Font("Kanit", 1, 40)
        chart.styler.chartTitlePadding = 15

        chart.styler.xAxisMin = 1.0
        chart.styler.xAxisMax = 26.0
        chart.styler.yAxisMin = 1.0
        chart.styler.yAxisMax = 26.0

        chart.styler.chartFontColor = Color.WHITE
        chart.styler.axisTickLabelsColor = Color.WHITE

        chart.styler.legendBackgroundColor = Color.GRAY

        chart.styler.isPlotGridLinesVisible = true
        chart.styler.isLegendVisible = true

        chart.styler.plotGridLinesColor = Color.GRAY
        chart.styler.chartBackgroundColor = Color.DARK_GRAY
        chart.styler.plotBackgroundColor = Color.DARK_GRAY

        chart.addSeries("Trajectory", refXList.toDoubleArray(), refYList.toDoubleArray())
        chart.addSeries("Robot", xList.toDoubleArray(), yList.toDoubleArray())

        val terror = trajectory.lastState.state.translation - totalpose.translation
        val rerror = trajectory.lastState.state.rotation - totalpose.rotation

        System.out.printf("%n[Test] X Error: %3.3f, Y Error: %3.3f%n", terror.x, terror.y)

        assert(terror.norm.also {
            println("[Test] Norm of Translational Error: $it")
        } < 0.50)
        assert(rerror.degrees.also {
            println("[Test] Rotational Error: $it degrees")
        } < 5.0)

        SwingWrapper(chart).displayChart()

        runBlocking { delay(100, TimeUnit.SECONDS) }
    }
}