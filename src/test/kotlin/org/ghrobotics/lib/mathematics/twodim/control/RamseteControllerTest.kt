/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control


import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryGeneratorTest
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.simulation.SimDifferentialDrive
import org.ghrobotics.lib.simulation.SimFalconMotor
import org.junit.Test
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import java.awt.Color
import java.awt.Font
import java.text.DecimalFormat

class RamseteControllerTest {

    private val kBeta = 2.0
    private val kZeta = 0.85

    @Test
    fun testTrajectoryFollower() {
        val ramseteTracker = RamseteTracker(
            kBeta,
            kZeta
        )

        val drive = SimDifferentialDrive(
            TrajectoryGeneratorTest.drive,
            SimFalconMotor(),
            SimFalconMotor(),
            ramseteTracker,
            1.05
        )

        var currentTime = 0.second
        val deltaTime = 20.millisecond

        val xList = arrayListOf<Double>()
        val yList = arrayListOf<Double>()

        val refXList = arrayListOf<Double>()
        val refYList = arrayListOf<Double>()

        ramseteTracker.reset(TrajectoryGeneratorTest.trajectory)

        drive.robotPosition = ramseteTracker.referencePoint!!.state.state.pose
            .transformBy(Pose2d(1.feet, 50.inch, 5.degree))

        while (!ramseteTracker.isFinished) {
            currentTime += deltaTime
            drive.setOutput(ramseteTracker.nextState(drive.robotPosition, currentTime))
            drive.update(deltaTime)

            xList += drive.robotPosition.translation.x / SILengthConstants.kFeetToMeter
            yList += drive.robotPosition.translation.y / SILengthConstants.kFeetToMeter

            val referenceTranslation = ramseteTracker.referencePoint!!.state.state.pose.translation
            refXList += referenceTranslation.x / SILengthConstants.kFeetToMeter
            refYList += referenceTranslation.y / SILengthConstants.kFeetToMeter

            // TODO add voltage to the sim
//            System.out.printf(
//                "Left Voltage: %3.3f, Right Voltage: %3.3f%n",
//                drive.leftMotor.voltageOutput.value, drive.rightMotor.voltageOutput.value
//            )
        }

        val fm = DecimalFormat("#.###").format(TrajectoryGeneratorTest.trajectory.lastInterpolant.second)

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

        val terror =
            TrajectoryGeneratorTest.trajectory.lastState.state.pose.translation - drive.robotPosition.translation
        val rerror = TrajectoryGeneratorTest.trajectory.lastState.state.pose.rotation - drive.robotPosition.rotation

        System.out.printf(
            "%n[Test] X Error: %3.3f, Y Error: %3.3f%n",
            terror.x / SILengthConstants.kFeetToMeter,
            terror.y / SILengthConstants.kFeetToMeter
        )

        assert(terror.norm.also {
            println("[Test] Norm of Translational Error: $it")
        } < 0.50)
        assert(rerror.degree.also {
            println("[Test] Rotational Error: $it degrees")
        } < 5.0)

//        SwingWrapper(chart).displayChart()
//        Thread.sleep(1000000)
    }
}