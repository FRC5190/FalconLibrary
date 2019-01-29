package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit

fun <T : SIUnit<T>> T.toNativeUnitPosition(model: NativeUnitModel<T>) = model.toNativeUnitPosition(this)

@Deprecated("Use nativeUnits naming instead of STU", ReplaceWith("nativeUnits"))
val Number.STU get() = nativeUnits
val Number.nativeUnits get() = NativeUnit(toDouble())

class NativeUnit(
    override val value: Double
) : SIUnit<NativeUnit> {
    override fun createNew(newValue: Double) = NativeUnit(newValue)

    fun <T : SIUnit<T>> fromNativeUnit(model: NativeUnitModel<T>) = model.fromNativeUnitPosition(this)

    companion object {
        val kZero = NativeUnit(0.0)
    }
}