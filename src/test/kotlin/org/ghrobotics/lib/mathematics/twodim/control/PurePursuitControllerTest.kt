package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.*
import org.ghrobotics.lib.mathematics.twodim.trajectory.DefaultTrajectoryGenerator
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.CentripetalAccelerationConstraint
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test
import org.knowm.xchart.XYChartBuilder
import java.awt.Color
import java.awt.Font
import java.text.DecimalFormat

class PurePursuitControllerTest {

    private lateinit var trajectoryFollower: ITrajectoryFollower

    private val kLat = 1.3
    private val kLookaheadTime = 0.3.second

    @Test
    fun testTrajectoryFollower() {
        val kSideStart = Pose2d(1.54.feet, 23.234167.feet, 180.degrees)
        val kNearScaleEmpty = Pose2d(23.7.feet, 20.2.feet, 160.degrees)

        val name = "T"
        val trajectory = DefaultTrajectoryGenerator.generateTrajectory(
            listOf(
                kSideStart,
                kSideStart + Pose2d((-13).feet, 0.feet, 0.degrees),
                kSideStart + Pose2d((-19.5).feet, 5.feet, (-90).degrees),
                kSideStart + Pose2d((-19.5).feet, 14.feet, (-90).degrees),
                kNearScaleEmpty.mirror
            ),
            listOf(CentripetalAccelerationConstraint(4.feet.acceleration)),
            0.0.feet.velocity,
            0.0.feet.velocity,
            10.0.feet.velocity,
            4.0.feet.acceleration,
            true
        )
        val iterator = trajectory.iterator()
        trajectoryFollower = PurePursuitController(trajectory, kLat, kLookaheadTime)

        val error = Pose2d(Translation2d(1.0, 6.0), Rotation2d.fromDegrees(5.0))
        var totalpose = trajectory.firstState.state.pose.transformBy(error)

        var time = 0.second
        val dt = 20.millisecond

        val xList = arrayListOf<Double>()
        val yList = arrayListOf<Double>()

        val refXList = arrayListOf<Double>()
        val refYList = arrayListOf<Double>()

        while (!iterator.isDone) {
            val pt = iterator.advance(dt)
            val output = trajectoryFollower.getSteering(totalpose, time) * dt.second.asDouble
            time += dt

            totalpose += Twist2d(
                output.dxRaw,
                output.dyRaw,
                output.dThetaRaw * 1.05
            ).asPose

            xList.add(totalpose.translation.x.feet.asDouble)
            yList.add(totalpose.translation.y.feet.asDouble)

            refXList.add(pt.state.state.pose.translation.x.feet.asDouble)
            refYList.add(pt.state.state.pose.translation.y.feet.asDouble)
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

        val terror = trajectory.lastState.state.pose.translation - totalpose.translation
        val rerror = trajectory.lastState.state.pose.rotation - totalpose.rotation

        System.out.printf("%n[Test] X Error: %3.3f, Y Error: %3.3f%n", terror.x.feet.asDouble, terror.y.feet.asDouble)

        assert(terror.norm.also {
            println("[Test] Norm of Translational Error: $it")
        } < 0.50)
        assert(rerror.degrees.also {
            println("[Test] Rotational Error: $it degrees")
        } < 5.0)
//
//        SwingWrapper(chart).displayChart()
//        Thread.sleep(1000000)
    }
}