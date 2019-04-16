package org.ghrobotics.lib.mathematics.units.nativeunits

object DefaultNativeUnitModel : NativeUnitModel<NativeUnit>(NativeUnit.kZero) {
    override fun fromNativeUnitPosition(nativeUnits: Double): Double = nativeUnits
    override fun toNativeUnitPosition(modelledUnit: Double): Double = modelledUnit
}