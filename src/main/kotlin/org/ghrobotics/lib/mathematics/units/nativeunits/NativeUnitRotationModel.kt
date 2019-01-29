package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.Rotation2d

class NativeUnitRotationModel(
    nativeUnitsPerRotation: NativeUnit
) : NativeUnitModel<Rotation2d>(Rotation2d.kZero) {

    private val nativeUnitsPerRotation = nativeUnitsPerRotation.value

    override fun toNativeUnitPosition(modelledUnit: Double): Double =
        (modelledUnit / (2.0 * Math.PI)) * nativeUnitsPerRotation

    override fun fromNativeUnitPosition(nativeUnits: Double): Double =
        2.0 * Math.PI * (nativeUnits / nativeUnitsPerRotation)

}