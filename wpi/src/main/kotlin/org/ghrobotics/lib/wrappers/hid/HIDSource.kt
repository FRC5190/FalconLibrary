package org.ghrobotics.lib.wrappers.hid

import edu.wpi.first.wpilibj.GenericHID
import org.ghrobotics.lib.mathematics.max
import org.ghrobotics.lib.mathematics.min
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.safeRangeTo

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

class BoundedHIDAxisSource(
        private val genericHID: GenericHID,
        private val axisId: Int,
        private val minValue: Double,
        private val maxValue: Double
) : HIDSource {
    override fun invoke() = min(max(genericHID.getRawAxis(axisId), minValue), maxValue)
}

class HIDPOVSource(
    private val genericHID: GenericHID,
    private val povId: Int,
    private val angle: Int
) : HIDSource {
    override fun invoke() = if (genericHID.getPOV(povId) == angle) 1.0 else 0.0
}

interface HIDSource : Source<Double>