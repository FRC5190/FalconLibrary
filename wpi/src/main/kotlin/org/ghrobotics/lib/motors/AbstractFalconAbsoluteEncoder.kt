/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors

import edu.wpi.first.util.sendable.Sendable
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel

abstract class AbstractFalconAbsoluteEncoder<K : SIKey>(model: NativeUnitModel<K>) :
    AbstractFalconEncoder<K>(model),
    Sendable {

    abstract val absolutePosition: SIUnit<Radian>
}
