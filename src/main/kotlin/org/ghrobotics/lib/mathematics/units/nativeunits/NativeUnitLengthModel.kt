package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.Length

class NativeUnitLengthModel(
    nativeUnitsPerRotation: NativeUnit,
    wheelRadius: Length
) : NativeUnitModel<Length>(Length(0.0)) {

    private val nativeUnitsPerRotation = nativeUnitsPerRotation.value
    private val wheelRadius = wheelRadius.value

    override fun fromNativeUnit(nativeUnits: Double): Double =
        wheelRadius * ((nativeUnits / nativeUnitsPerRotation) * (2.0 * Math.PI))

    override fun toNativeUnit(modelledUnit: Double): Double =
        nativeUnitsPerRotation * (modelledUnit / (wheelRadius * (2.0 * Math.PI)))

}