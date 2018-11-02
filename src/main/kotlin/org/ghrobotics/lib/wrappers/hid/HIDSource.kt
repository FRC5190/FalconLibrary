package org.ghrobotics.lib.wrappers.hid

import edu.wpi.first.wpilibj.GenericHID
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source

class HIDButtonSource(
    private val genericHID: GenericHID,
    private val buttonId: Int
) : HIDSource {
    val booleanSource: BooleanSource = { genericHID.getRawButton(buttonId) }

    override fun invoke() = if (booleanSource()) 1.0 else 0.0
}

class HIDAxisSource(
    private val genericHID: GenericHID,
    private val axisId: Int
) : HIDSource {
    override fun invoke() = genericHID.getRawAxis(axisId)
}

class HIDPOVSource(
    private val genericHID: GenericHID,
    private val povId: Int,
    private val angle: Int
) : HIDSource {
    override fun invoke() = if (genericHID.getPOV(povId) == angle) 1.0 else 0.0
}

interface HIDSource : Source<Double>