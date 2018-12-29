package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.inch

class NativeUnitLengthModel internal constructor(
    sensorUnitsPerRotation: NativeUnit,
    internal val _wheelRadius: Double
) : NativeUnitModel<Length>(sensorUnitsPerRotation) {

    constructor(
        sensorUnitsPerRotation: NativeUnit = NativeUnitModel.kDefaultSensorUnitsPerRotation,
        wheelRadius: Length = 3.inch
    ) : this(sensorUnitsPerRotation, wheelRadius.value)

    override fun createNew(newValue: Double) = Length(newValue)

    override fun toModel(value: Double) = _wheelRadius * ((value / _sensorUnitsPerRotation) * (2.0 * Math.PI))
    override fun fromModel(value: Double) = _sensorUnitsPerRotation * (value / (_wheelRadius * (2.0 * Math.PI)))
}

class NativeUnitRotationModel(
    sensorUnitsPerRotation: NativeUnit = NativeUnitModel.kDefaultSensorUnitsPerRotation
) : NativeUnitModel<Rotation2d>(sensorUnitsPerRotation) {
    override fun createNew(newValue: Double) = Rotation2d(newValue)

    override fun toModel(value: Double) = 2.0 * Math.PI * value / _sensorUnitsPerRotation
    override fun fromModel(value: Double) = _sensorUnitsPerRotation * value / (2.0 * Math.PI)
}

abstract class NativeUnitModel<T : SIUnit<T>> internal constructor(
    internal val _sensorUnitsPerRotation: Double
) {
    constructor(sensorUnitsPerRotation: NativeUnit) : this(sensorUnitsPerRotation.value)

    val zero get() = createNew(0.0)

    protected abstract fun createNew(newValue: Double): T

    internal abstract fun toModel(value: Double): Double
    internal abstract fun fromModel(value: Double): Double

    /**
     * Converts NativeUnits to the modelled unit
     */
    fun toModel(value: NativeUnit) = createNew(toModel(value.value))

    /**
     * Converts the modelled unit to NativeUnits
     */
    fun fromModel(value: T) = NativeUnit(fromModel(value.value))

    companion object {
        val kDefaultSensorUnitsPerRotation = 1440.STU
    }
}