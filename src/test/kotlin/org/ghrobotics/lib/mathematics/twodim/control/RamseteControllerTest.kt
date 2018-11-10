/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

/* ktlint-disable no-wildcard-imports */
import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryGeneratorTest
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryGeneratorTest.Companion.trajectory
import org.ghrobotics.lib.mathematics.units.*
import org.junit.Test
import org.knowm.xchart.XYChartBuilder
import java.awt.Color
import java.awt.Font
import java.text.DecimalFormat

class RamseteControllerTest {

    private lateinit var trajectoryFollower: TrajectoryFollower

    private val kBeta = 2.0
    private val kZeta = 0.85

    @Test
    fun testTrajectoryFollower() {
        val iterator = TrajectoryGeneratorTest.trajectory.iterator()
        trajectoryFollower = RamseteController(TrajectoryGeneratorTest.drive, kBeta, kZeta)
        trajectoryFollower.resetTrajectory(trajectory)

        val error = Pose2d(1.feet, 50.inch, 5.degree)
        var totalpose = iterator.currentState.state.state.pose.transformBy(error)

        var time = 0.second
        val dt = 20.millisecond

        val xList = arrayListOf<Double>()
        val yList = arrayListOf<Double>()

        val refXList = arrayListOf<Double>()
        val refYList = arrayListOf<Double>()

        while (!iterator.isDone) {

            val pt = iterator.advance(dt)
            val output = trajectoryFollower.getOutputFromKinematics(totalpose, time)

            val wheelstate = DifferentialDrive.WheelState(
                    output.leftSetPoint * dt / 3.inch,
                    output.rightSetPoint * dt / 3.inch
            )

            val k = TrajectoryGeneratorTest.drive.solveForwardKinematics(wheelstate)

            time += dt

            totalpose += Twist2d(
                    k.linear.meter,
                    0.meter,
                    k.angular.radian * 1.05
            ).asPose

            xList.add(totalpose.translation.x.feet)
            yList.add(totalpose.translation.y.feet)

            refXList.add(pt.state.state.pose.translation.x.feet)
            refYList.add(pt.state.state.pose.translation.y.feet)

            System.out.printf("Left Voltage: %3.3f, Right Voltage: %3.3f%n",
                    output.leftVoltage.value, output.rightVoltage.value)
        }

        val fm = DecimalFormat("#.###").format(trajectory.lastInterpolant.second)

        val chart = XYChartBuilder().width(1800).height(1520).title("$fm seconds.")
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

        val terror = trajectory.lastState.state.pose.translation - totalpose.translation
        val rerror = trajectory.lastState.state.pose.rotation - totalpose.rotation

        System.out.printf("%n[Test] X Error: %3.3f, Y Error: %3.3f%n", terror.x.feet, terror.y.feet)

        assert(terror.norm.value.also {
            println("[Test] Norm of Translational Error: $it")
        } < 0.50)
        assert(rerror.degree.also {
            println("[Test] Rotational Error: $it degrees")
        } < 5.0)
//
        // SwingWrapper(chart).displayChart()
        // Thread.sleep(1000000)
    }
}