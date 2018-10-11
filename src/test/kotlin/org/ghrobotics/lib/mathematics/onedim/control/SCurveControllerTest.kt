package org.ghrobotics.lib.mathematics.onedim.control

import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.onedim.geometry.Displacement1d
import org.junit.Test
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder
import kotlin.math.absoluteValue

/*
class DynamicSCurveControllerTest {
    @Test
    fun testSCurve() {

        val totalDistance = 40.0
        val maxVelocity = 10.0
        val maxAcceleration = 10.0
        val jerk = 10.0

        val controller: IKinematicController = SCurveController(totalDistance, maxVelocity, maxAcceleration, jerk)

        var time = 0.0
        val dt = 0.005

        var pose = Displacement1d()

        var lastVelocity = 0.0
        var pvadata: PVAData

        val xList = arrayListOf<Double>()
        val vList = arrayListOf<Double>()
        val tList = arrayListOf<Double>()

        while (true) {
            pvadata = controller.getVelocity(time.toLong())
            pose = Displacement1d(pvadata.x)

//            assert(((pvadata.v - lastVelocity) / dt).absoluteValue <= maxAcceleration + kEpsilon)

            tList.add(time / 1E9)
            xList.add(pose.x)
            vList.add(pvadata.v)

            time += dt * 1.0e+9

            if (pose.x > 0 && pvadata.v == 0.0) break
            lastVelocity = pvadata.v

            Thread.sleep(1)
        }


        assert(pose.x < totalDistance + 0.1).also { println("Total Distance Traveled: $pose") }
        val chart = XYChartBuilder().width(1800).height(1520).title("${pose.x}")
                .xAxisTitle("X").yAxisTitle("Y").build()


        chart.addSeries("X", tList.toDoubleArray(), xList.toDoubleArray())
        chart.addSeries("V", tList.toDoubleArray(), vList.toDoubleArray())

        SwingWrapper(chart).displayChart("T Curve Controller Test")

        Thread.sleep(10000000)

    }
}
*/
