/*
 * FRC Team 5190
 * Green Hope Falcons
 */

@file:Suppress("MemberVisibilityCanBePrivate", "unused", "PropertyName")

package frc.team5190.lib.math.units

import kotlin.math.absoluteValue
import kotlin.math.roundToInt

interface Distance {

    val FT: Double
    val IN: Double
    val STU: Int

    val settings: UnitPreferences

    val absoluteValue: Distance
        get() = NativeUnits(this.STU.absoluteValue, this.settings)


    operator fun plus(other: Distance): Distance {
        return NativeUnits(this.STU + other.STU, this.settings)
    }

    operator fun minus(other: Distance): Distance {
        return NativeUnits(this.STU - other.STU, this.settings)
    }

    operator fun times(other: Distance): Distance {
        return NativeUnits(this.STU * other.STU, this.settings)
    }

    operator fun div(other: Distance): Distance {
        return NativeUnits(this.STU / other.STU, this.settings)
    }

    operator fun times(scalar: Double): Distance {
        return Feet(this.FT * scalar, this.settings)
    }

    operator fun div(scalar: Double): Distance {
        return Feet(this.FT / scalar, this.settings)
    }

    operator fun unaryPlus(): Distance {
        return this
    }

    operator fun unaryMinus(): Distance {
        return NativeUnits(-this.STU, this.settings)
    }

    operator fun compareTo(other: Distance): Int {
        return this.STU - other.STU
    }

    fun withSettings(settings: UnitPreferences): Distance
}

class NativeUnits(private val value: Int, override val settings: UnitPreferences = UnitPreferences()) : Distance {
    override val STU
        get() = value
    override val FT
        get() = value.toDouble() / settings.sensorUnitsPerRotation.toDouble() * (2.0 * Math.PI * settings.radius) / 12.0
    override val IN
        get() = FT * 12.0

    override fun withSettings(settings: UnitPreferences) = NativeUnits(value, settings)
}

class Inches(private val value: Double, override val settings: UnitPreferences = UnitPreferences()) : Distance {
    override val IN
        get() = value
    override val FT
        get() = value / 12.0
    override val STU
        get() = Feet(FT, settings).STU

    override fun withSettings(settings: UnitPreferences) = Inches(value, settings)
}

class Feet(private val value: Double, override val settings: UnitPreferences = UnitPreferences()) : Distance {
    override val FT
        get() = value
    override val IN
        get() = value * 12.0
    override val STU
        get() = (value * 12.0 / (2.0 * Math.PI * settings.radius) * settings.sensorUnitsPerRotation.toDouble()).roundToInt()

    override fun withSettings(settings: UnitPreferences) = Feet(value, settings)
}