package org.ghrobotics.lib.mathematics.onedim.control

interface DynamicKinematicsController {
    fun getVelocity(current: Double): Double
}