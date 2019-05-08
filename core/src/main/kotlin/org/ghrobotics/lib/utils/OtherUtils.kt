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
            } else letter.toLowerCase()
        )
        if (letter.isWhitespace()) previousWasSpace = true
    }
}