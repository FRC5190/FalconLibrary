/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.CentripetalAccelerationConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.DifferentialDriveDynamicsConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.optimization.PathFinder
import org.ghrobotics.lib.mathematics.units.derived.*
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.inSeconds
import org.ghrobotics.lib.mathematics.units.inches
import org.ghrobotics.lib.mathematics.units.milli
import org.junit.Test
import org.knowm.xchart.XYChartBuilder
import java.awt.Color
import java.awt.Font
import java.text.DecimalFormat
import kotlin.system.measureTimeMillis

class PathFinderTest {

    @Test
    fun testPathFinder() {
        val robotSize = 33.0.inches
        val pathFinder = PathFinder(
            robotSize,
            PathFinder.k2018LeftSwitch,
            PathFinder.k2018Platform,
            PathFinder.k2018CubesSwitch
        )
        lateinit var path: List<Pose2d>
        val nodeCreationTime = measureTimeMillis {
            path = pathFinder.findPath(
                Pose2d(1.54.feet, 23.234167.feet, 0.0.degrees.toRotation2d()),
                Pose2d(23.7.feet, (27 - 20.2).feet, 0.0.degrees.toRotation2d()),
                Rectangle2d(
                    Translation2d(0.0.feet, 0.0.feet),
                    Translation2d(10.0.feet, 10.0.feet)
                )
            )!!
            println(path.joinToString(separator = "\n") {
                "${it.translation.x}\t${it.translation.y}\t${it.rotation.degrees}"
            })
        }
        println("Generated Nodes in $nodeCreationTime ms")
        lateinit var trajectory: Trajectory
        val trajectoryTime = measureTimeMillis {
            trajectory = DefaultTrajectoryGenerator.generateTrajectory(
                path,
                listOf(
                    CentripetalAccelerationConstraint(4.0.feet.acceleration),
                    DifferentialDriveDynamicsConstraint(TrajectoryGeneratorTest.drive, 9.0.volts)
                ),
                0.0.feet.velocity,
                0.0.feet.velocity,
                10.0.feet.velocity,
                4.0.feet.acceleration,
                false
            )
        }
        println(
            "Generated Trajectory in $trajectoryTime ms\n" +
                    "Total: ${trajectoryTime + nodeCreationTime} ms"
        )

        val iterator = trajectory
        val dt = 20.0.milli.seconds
        val refList = mutableListOf<Translation2d>()

        while (!iterator.isDone) {
            val pt = iterator.advance(dt)
            refList.add(pt.state.pose.translation)
        }

        val fm = DecimalFormat("#.###").format(TrajectoryGeneratorTest.trajectory.lastInterpolant.inSeconds())

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

        chart.addSeries(
            "Trajectory",
            refList.map { it.x }.toDoubleArray(),
            refList.map { it.y }.toDoubleArray()
        )
//        SwingWrapper(chart).displayChart()
//        Thread.sleep(1000000)
    }
}