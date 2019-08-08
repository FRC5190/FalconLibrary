package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.can.VictorSPX
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel

class FalconSPX<K : SIKey>(
    val victorSPX: VictorSPX,
    model: NativeUnitModel<K>
) : FalconCTRE<K>(victorSPX, model) {

    constructor(id: Int, model: NativeUnitModel<K>) : this(VictorSPX(id), model)

}