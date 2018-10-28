package org.ghrobotics.lib.sensors

import com.ctre.phoenix.sensors.PigeonIMU
import com.kauailabs.navx.frc.AHRS
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.degree
import org.ghrobotics.lib.utils.Source

interface AHRSSensor : Source<Rotation2d> {
    var angleOffset: Rotation2d
    val correctedAngle: Rotation2d
    fun reset()
    override fun invoke() = correctedAngle
}

abstract class AHRSSensorImpl : AHRSSensor {
    protected abstract val sensorYaw: Rotation2d
    override var angleOffset = 0.degree
    override val correctedAngle: Rotation2d get() = (sensorYaw + angleOffset).degree
}

fun PigeonIMU.toFalconSensor(): AHRSSensor = Pigeon(this)
fun AHRS.toFalconSensor(): AHRSSensor = NavX(this)

private class Pigeon(val pigeon: PigeonIMU) : AHRSSensorImpl() {
    init {
        reset()
    }

    override val sensorYaw get() = pigeon.fusedHeading.degree

    override fun reset() {
        pigeon.setYaw(0.0, 10)
    }
}

private class NavX(val navX: AHRS) : AHRSSensorImpl() {
    init {
        reset()
    }

    override val sensorYaw get() = (-navX.fusedHeading).degree

    override fun reset() {
        navX.reset()
    }
}