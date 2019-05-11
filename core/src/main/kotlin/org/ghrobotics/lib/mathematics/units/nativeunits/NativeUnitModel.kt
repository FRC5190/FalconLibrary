package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity

abstract class NativeUnitModel<T : SIUnit<T>>(
    internal val zero: T
) {
    // FAST DOUBLE METHODS

    abstract fun fromNativeUnitPosition(nativeUnits: Double): Double
    abstract fun toNativeUnitPosition(modelledUnit: Double): Double

    open fun toNativeUnitError(modelledUnit: Double): Double =
        toNativeUnitPosition(modelledUnit) - toNativeUnitPosition(0.0)

    open fun fromNativeUnitVelocity(nativeUnitVelocity: Double) = fromNativeUnitPosition(nativeUnitVelocity)
    open fun toNativeUnitVelocity(modelledUnitVelocity: Double) = toNativeUnitPosition(modelledUnitVelocity)

    open fun fromNativeUnitAcceleration(nativeUnitAcceleration: Double) =
        fromNativeUnitVelocity(nativeUnitAcceleration)

    open fun toNativeUnitAcceleration(modelledUnitAcceleration: Double) =
        toNativeUnitVelocity(modelledUnitAcceleration)

    // TYPED METHODS

    fun fromNativeUnitPosition(nativeUnits: NativeUnit) = zero.createNew(fromNativeUnitPosition(nativeUnits.value))
    fun toNativeUnitPosition(modelledUnit: T) = NativeUnit(toNativeUnitPosition(modelledUnit.value))

    fun toNativeUnitError(modelledUnit: T) = NativeUnit(toNativeUnitError(modelledUnit.value))

    fun fromNativeUnitVelocity(nativeUnitVelocity: NativeUnitVelocity) =
        Velocity(fromNativeUnitVelocity(nativeUnitVelocity.value), zero)

    fun toNativeUnitVelocity(modelledUnitVelocity: Velocity<T>) =
        NativeUnitVelocity(toNativeUnitVelocity(modelledUnitVelocity.value), NativeUnit.kZero)

    fun fromNativeUnitAcceleration(nativeUnitAcceleration: NativeUnitAcceleration) =
        Acceleration(fromNativeUnitAcceleration(nativeUnitAcceleration.value), zero)

    fun toNativeUnitAcceleration(modelledUnitAcceleration: Acceleration<T>) =
        NativeUnitAcceleration(toNativeUnitAcceleration(modelledUnitAcceleration.value), NativeUnit.kZero)
}