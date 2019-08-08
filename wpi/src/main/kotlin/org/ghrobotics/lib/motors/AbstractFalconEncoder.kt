package org.ghrobotics.lib.motors

import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel

abstract class AbstractFalconEncoder<K : SIKey>(
    val model: NativeUnitModel<K>
) : FalconEncoder<K> {
    override val position: SIUnit<K> get() = model.fromNativeUnitPosition(rawPosition)
    override val velocity: SIUnit<Velocity<K>> get() = model.fromNativeUnitVelocity(rawVelocity)

    override fun resetPosition(newPosition: SIUnit<K>) {
        resetPositionRaw(model.toNativeUnitPosition(newPosition))
    }
}