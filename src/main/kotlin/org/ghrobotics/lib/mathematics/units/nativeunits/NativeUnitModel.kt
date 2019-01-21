package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity

abstract class NativeUnitModel<T : SIUnit<T>>(
    internal val zero: T
) {
    // FAST DOUBLE METHODS

    abstract fun fromNativeUnit(nativeUnits: Double): Double
    abstract fun toNativeUnit(modelledUnit: Double): Double

    open fun fromNativeUnitVelocity(nativeUnitVelocity: Double) = fromNativeUnit(nativeUnitVelocity)
    open fun toNativeUnitVelocity(modelledUnitVelocity: Double) = toNativeUnit(modelledUnitVelocity)

    open fun fromNativeUnitAcceleration(nativeUnitAcceleration: Double) =
        fromNativeUnitVelocity(nativeUnitAcceleration)

    open fun toNativeUnitAcceleration(modelledUnitAcceleration: Double) =
        toNativeUnitVelocity(modelledUnitAcceleration)

    // TYPED METHODS

    fun fromNativeUnit(nativeUnits: NativeUnit) = zero.createNew(fromNativeUnit(nativeUnits.value))
    fun toNativeUnit(modelledUnit: T) = NativeUnit(toNativeUnit(modelledUnit.value))

    fun fromNativeUnitVelocity(nativeUnitVelocity: NativeUnitVelocity) =
        Velocity(fromNativeUnit(nativeUnitVelocity.value), zero)

    fun toNativeUnitVelocity(modelledUnitVelocity: Velocity<T>) =
        NativeUnitVelocity(toNativeUnit(modelledUnitVelocity.value), NativeUnit.ZERO)

    fun fromNativeUnitAcceleration(nativeUnitAcceleration: NativeUnitAcceleration) =
        Acceleration(fromNativeUnitVelocity(nativeUnitAcceleration.value), zero)

    fun toNativeUnitAcceleration(modelledUnitAcceleration: Acceleration<T>) =
        NativeUnitAcceleration(toNativeUnitVelocity(modelledUnitAcceleration.value), NativeUnit.ZERO)
}