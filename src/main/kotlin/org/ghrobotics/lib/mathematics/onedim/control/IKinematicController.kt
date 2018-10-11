package org.ghrobotics.lib.mathematics.onedim.control

interface IKinematicController {
    fun getVelocity(nanotime: Long = System.nanoTime()): PVAData
}