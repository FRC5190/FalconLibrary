/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers.hid

import edu.wpi.first.wpilibj.GenericHID
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source

class HIDButtonSource(
    private val genericHID: GenericHID,
    private val buttonId: Int,
) : HIDSource {
    val booleanSource: BooleanSource = { genericHID.getRawButton(buttonId) }

    override fun invoke() = if (booleanSource()) 1.0 else 0.0
}

class HIDAxisSource(
    private val genericHID: GenericHID,
    private val axisId: Int,
) : HIDSource {
    override fun invoke() = genericHID.getRawAxis(axisId)
}

class BoundedHIDAxisSource(
    private val genericHID: GenericHID,
    private val axisId: Int,
    private val range: ClosedFloatingPointRange<Double>,
) : HIDSource {
    override fun invoke() = genericHID.getRawAxis(axisId).coerceIn(range)
}

class HIDPOVSource(
    private val genericHID: GenericHID,
    private val povId: Int,
    private val angle: Int,
) : HIDSource {
    override fun invoke() = if (genericHID.getPOV(povId) == angle) 1.0 else 0.0
}

interface HIDSource : Source<Double>
