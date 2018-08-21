/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.lib.math.units

interface Current {

    val amps: Int

    operator fun plus(other: Current) = Amps(this.amps + other.amps)
    operator fun minus(other: Current) = Amps(this.amps - other.amps)
    operator fun unaryMinus() = Amps(-this.amps)
}

class Amps (val value: Int) : Current {
    override val amps = value
}

interface Voltage {
    val volts: Double

    operator fun plus(other: Voltage) = Volts(this.volts + other.volts)
    operator fun minus(other: Voltage) = Volts(this.volts - other.volts)
    operator fun unaryMinus() = Volts(-this.volts)
}

class Volts (val value: Double) : Voltage {
    override val volts = value
}


