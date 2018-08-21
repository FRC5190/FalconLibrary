/*
 * FRC Team 5190
 * Green Hope Falcons
 */

@file:Suppress("MemberVisibilityCanBePrivate", "unused", "PropertyName")

package frc.team5190.lib.math.units

interface Speed {
    val STU: Int
    val IPS: Double
    val FPS: Double

    val settings: UnitPreferences

    operator fun plus(other: Speed): Speed {
        return NativeUnitsPer100Ms(this.STU + other.STU, this.settings)
    }

    operator fun minus(other: Speed): Speed {
        return NativeUnitsPer100Ms(this.STU - other.STU, this.settings)
    }

    operator fun times(other: Speed): Speed {
        return NativeUnitsPer100Ms(this.STU * other.STU, this.settings)
    }

    operator fun div(other: Speed): Speed {
        return NativeUnitsPer100Ms(this.STU / other.STU, this.settings)
    }

    operator fun times(scalar: Double): Speed {
        return FeetPerSecond(this.FPS * scalar, this.settings)
    }

    operator fun div(scalar: Double): Speed {
        return FeetPerSecond(this.FPS / scalar, this.settings)
    }

    operator fun unaryPlus(): Speed {
        return this
    }

    operator fun unaryMinus(): Speed {
        return NativeUnitsPer100Ms(-this.STU, this.settings)
    }

    operator fun compareTo(other: Speed): Int {
        return this.STU - other.STU
    }
}


class NativeUnitsPer100Ms(val value: Int, override val settings: UnitPreferences = UnitPreferences()) : Speed {
    override val STU
        get() = value
    override val FPS
        get() = (value.toDouble() / settings.sensorUnitsPerRotation.toDouble() * (2.0 * Math.PI * settings.radius) / 12.0) * 10.0
    override val IPS
        get() = FPS * 12.0
}


class InchesPerSecond(val value: Double, override val settings: UnitPreferences = UnitPreferences()) : Speed {
    override val IPS
        get() = value
    override val FPS
        get() = value / 12.0
    override val STU
        get() = FeetPerSecond(FPS, settings).STU


}

class FeetPerSecond(val value: Double, override val settings: UnitPreferences = UnitPreferences()) : Speed {
    override val FPS
        get() = value
    override val STU
        get() = ((value * 6.0 * settings.sensorUnitsPerRotation) / (10 * Math.PI * settings.radius)).toInt()
    override val IPS
        get() = value * 12.0
}


