package org.ghrobotics.lib.motors

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel

abstract class AbstractFalconEncoder<T : SIUnit<T>>(
    val model: NativeUnitModel<T>
) : FalconEncoder<T> {
    override val position: Double get() = model.fromNativeUnitPosition(rawPosition)
    override val velocity: Double get() = model.fromNativeUnitVelocity(rawVelocity)
}