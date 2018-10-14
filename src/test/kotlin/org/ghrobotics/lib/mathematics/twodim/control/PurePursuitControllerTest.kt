package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.geometry.degrees
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryGeneratorTest
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.inch
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import java.awt.Color
import java.awt.Font
import java.text.DecimalFormat


class PurePursuitControllerTest {

    private lateinit var trajectoryFollower: TrajectoryFollower

    private val kLat = 4.0
    private val kLookaheadTime = 0.3.second

    @Test
    fun testTrajectoryFollower() {
        val iterator = TrajectoryGeneratorTest.trajectory.iterator()
        trajectoryFollower = PurePursuitController(TrajectoryGeneratorTest.trajectory, TrajectoryGeneratorTest.drive, kLat, kLookaheadTime)

        val error =  Pose2d(1.feet, 50.inch, 5.degrees)
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
                    output.lSetpoint.asDouble * dt.second.asDouble / 3.inch.meter.asDouble,
                    output.rSetpoint.asDouble * dt.second.asDouble / 3.inch.meter.asDouble)

            val k = TrajectoryGeneratorTest.drive.solveForwardKinematics(wheelstate)

            time += dt

            totalpose += Twist2d(
                    k.linear,
                    0.0,
                    k.angular * 1.05
            ).asPose


            xList.add(totalpose.translation.x.feet.asDouble)
            yList.add(totalpose.translation.y.feet.asDouble)

            refXList.add(pt.state.state.pose.translation.x.feet.asDouble)
            refYList.add(pt.state.state.pose.translation.y.feet.asDouble)
        }

        val fm = DecimalFormat("#.###").format(TrajectoryGeneratorTest.trajectory.lastInterpolant.second.asDouble)

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

        val terror = TrajectoryGeneratorTest.trajectory.lastState.state.pose.translation - totalpose.translation
        val rerror = TrajectoryGeneratorTest.trajectory.lastState.state.pose.rotation - totalpose.rotation

        System.out.printf("%n[Test] X Error: %3.3f, Y Error: %3.3f%n", terror.x.feet.asDouble, terror.y.feet.asDouble)

        assert(terror.norm.also {
            println("[Test] Norm of Translational Error: $it")
        } < 0.50)
        assert(rerror.degrees.also {
            println("[Test] Rotational Error: $it degrees")
        } < 10.0)
////
//        SwingWrapper(chart).displayChart()
//        Thread.sleep(1000000)
    }
}

