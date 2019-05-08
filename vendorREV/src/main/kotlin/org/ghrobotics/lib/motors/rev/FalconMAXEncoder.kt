package org.ghrobotics.lib.motors.rev

import com.revrobotics.CANEncoder
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel
import org.ghrobotics.lib.motors.AbstractFalconEncoder

class FalconMAXEncoder<T : SIUnit<T>>(
    val canEncoder: CANEncoder,
    model: NativeUnitModel<T>
) : AbstractFalconEncoder<T>(model) {
    override val rawVelocity: Double get() = canEncoder.velocity / 60.0
    override val rawPosition: Double get() = canEncoder.position

    override fun resetPosition(newPosition: Double) {
        canEncoder.position = 0.0
    }

}