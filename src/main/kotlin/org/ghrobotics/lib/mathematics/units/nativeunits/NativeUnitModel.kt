package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.inch

class NativeUnitLengthModel(
    sensorUnitsPerRotation: Int = NativeUnitModel.kDefaultSensorUnitsPerRotation,
    val wheelRadius: Length = 3.inch
) : NativeUnitModel<Length>(sensorUnitsPerRotation, 0.inch) {
    override fun toModel(value: NativeUnit): Length =
        wheelRadius * ((value.asDouble / sensorUnitsPerRotation.toDouble()) * (2.0 * Math.PI))

    override fun fromModel(value: Length): NativeUnit =
        NativeUnitImpl(value / (wheelRadius * (2.0 * Math.PI)) * sensorUnitsPerRotation.toDouble())
}

abstract class NativeUnitModel<T : SIValue<T>>(
    val sensorUnitsPerRotation: Int,
    val zero: T
) {
    abstract fun toModel(value: NativeUnit): T
    abstract fun fromModel(value: T): NativeUnit

    companion object {
        const val kDefaultSensorUnitsPerRotation = 1440
    }
}