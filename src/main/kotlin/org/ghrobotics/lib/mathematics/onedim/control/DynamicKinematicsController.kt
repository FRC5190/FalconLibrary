package org.ghrobotics.lib.mathematics.onedim.control

interface DynamicKinematicsController {
    fun getVelocity(currentPos: Double, nanotime: Long = System.nanoTime()): Double
}