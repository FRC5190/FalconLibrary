package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.UnboundedRotation

@Deprecated("")
class NativeUnitRotationModel(
    nativeUnitsPerRotation: NativeUnit
) : NativeUnitModel<UnboundedRotation>(UnboundedRotation.kZero) {

    private val nativeUnitsPerRotation = nativeUnitsPerRotation.value

    override fun toNativeUnitPosition(modelledUnit: Double): Double =
        (modelledUnit / (2.0 * Math.PI)) * nativeUnitsPerRotation

    override fun fromNativeUnitPosition(nativeUnits: Double): Double =
        2.0 * Math.PI * (nativeUnits / nativeUnitsPerRotation)

}