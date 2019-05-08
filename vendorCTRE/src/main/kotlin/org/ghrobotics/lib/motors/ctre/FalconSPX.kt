package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.can.VictorSPX
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel

class FalconSPX<T : SIUnit<T>>(
    val victorSPX: VictorSPX,
    model: NativeUnitModel<T>
) : FalconCTRE<T>(victorSPX, model) {

    constructor(id: Int, model: NativeUnitModel<T>) : this(VictorSPX(id), model)

}