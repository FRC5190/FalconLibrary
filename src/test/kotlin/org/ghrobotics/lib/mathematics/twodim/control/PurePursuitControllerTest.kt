package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.*
import org.ghrobotics.lib.mathematics.twodim.trajectory.TimedState
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryGenerator
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.CentripetalAccelerationConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.view.TimedView
import org.junit.Test
import org.knowm.xchart.XYChartBuilder
import java.awt.Color
import java.awt.Font
import java.text.DecimalFormat

class PurePursuitControllerTest {

    private lateinit var trajectoryFollower: ITrajectoryFollower

    private val kLat = 1.3
    private val kLookaheadTime = 0.3

    @Test
    fun testTrajectoryFollower() {
        val kSideStart = Pose2d(Translation2d(1.54, 23.234167), Rotation2d.fromDegrees(180.0))
        val kNearScaleEmpty = Pose2d(Translation2d(23.7, 20.2), Rotation2d.fromDegrees(160.0))

        val name = "T"
        val trajectory: Trajectory<TimedState<Pose2dWithCurvature>> = TrajectoryGenerator.generateTrajectory(
                true,
                arrayListOf(kSideStart,
                        kSideStart.transformBy(Pose2d(Translation2d(-13.0, 00.0), Rotation2d())),
                        kSideStart.transformBy(Pose2d(Translation2d(-19.5, 05.0), Rotation2d.fromDegrees(-90.0))),
                        kSideStart.transformBy(Pose2d(Translation2d(-19.5, 14.0), Rotation2d.fromDegrees(-90.0))),
                        kNearScaleEmpty.mirror),
                arrayListOf(CentripetalAccelerationConstraint(4.0)),
                0.0, 0.0,
                10.0, 4.0
        )!!
        val iterator = TrajectoryIterator(TimedView(trajectory))
        trajectoryFollower = PurePursuitController(trajectory, kLat, kLookaheadTime)

        val error = Pose2d(Translation2d(1.0, 6.0), Rotation2d.fromDegrees(5.0))
        var totalpose = trajectory.firstState.state.pose.transformBy(error)

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
                    output.dx,
                    output.dy,
                    output.dtheta * 1.05)))

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
//
//        SwingWrapper(chart).displayChart()
//        Thread.sleep(1000000)
    }
}