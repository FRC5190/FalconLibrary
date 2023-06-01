/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import kotlin.math.max
import kotlin.math.min

fun Double.safeRangeTo(endInclusive: Double) = min(this, endInclusive)..max(this, endInclusive)

fun String.capitalizeEachWord() = buildString(length) {
    var previousWasSpace = true
    for (letter in this@capitalizeEachWord) {
        append(
            if (previousWasSpace) {
                previousWasSpace = false
                letter.toUpperCase()
            } else letter.toLowerCase(),
        )
        if (letter.isWhitespace()) previousWasSpace = true
    }
}
