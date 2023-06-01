/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.unitlessValue
import org.ghrobotics.lib.types.Interpolatable
import java.util.TreeMap

/**
 * Creates an interpolating tree map. An interpolating tree map is similar
 * to a tree map, except we can interpolate between the values of two keys.
 *
 * @param interpolatingFunc A function that interpolates between two [V] values.
 */
open class InterpolatingTreeMap<K : SIKey, V>(val interpolatingFunc: (start: V, end: V, t: Double) -> V) :
    TreeMap<SIUnit<K>, V>() {
    /**
     * Returns an interpolated value based on the given key.
     *
     * @param key The key.
     * @return The interpolated value.
     */
    override operator fun get(key: SIUnit<K>): V? {
        // If the map is empty, return null.
        if (isEmpty()) return null

        // If we have the exact value that we're looking for, then return it.
        super.get(key)?.let { return it }

        // Get the top and bottom entries for this distance.
        val topBound = ceilingEntry(key)
        val bottomBound = floorEntry(key)

        return when {
            // When there are no more elements at the top, return the highest element.
            topBound == null -> bottomBound.value

            // When there are no more elements at the bottom, return the lowest element.
            bottomBound == null -> topBound.value

            // If there is a ceiling and a floor, interpolate between the two values.
            else -> interpolatingFunc(
                bottomBound.value,
                topBound.value,
                ((key - bottomBound.key) / (topBound.key - bottomBound.key)).unitlessValue,
            )
        }
    }

    operator fun set(key: SIUnit<K>, value: V) = this.put(key, value)

    companion object {
        /**
         * Creates an interpolating tree map that contains two SIUnit types.
         */
        fun <K : SIKey, V : SIKey> createFromSI() = InterpolatingTreeMap<K, SIUnit<V>> { start, end, t ->
            start.lerp(end, t)
        }

        /**
         * Creats an interpolating tree map from a type that implements [Interpolatable].
         */
        fun <K : SIKey, V : Interpolatable<V>> createFromInterpolatable() =
            InterpolatingTreeMap<K, V> { start, end, t ->
                start.interpolate(end, t)
            }
    }
}
