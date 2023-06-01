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
import org.ghrobotics.lib.types.Interpolatable

/**
 * Creates an [InterpolatingTreeMap], but with a certain buffer size.
 */
class InterpolatingTreeMapBuffer<K : SIKey, V>(
    interpolatingFunc: (start: V, end: V, t: Double) -> V,
    private val bufferSize: SIUnit<K>,
    private val source: Source<SIUnit<K>>,
) : InterpolatingTreeMap<K, V>(interpolatingFunc) {
    /**
     * Removes old entries from the map.
     */
    private fun clean() {
        // Get the current value of the unit represented by [K].
        val now = source()

        // Remove entries that are older than the buffer size.
        while (isNotEmpty()) {
            val entry = lastEntry()
            if (now - entry.key > bufferSize) {
                remove(entry.key)
            } else {
                return
            }
        }
    }

    /**
     * Adds a new value into the map.
     *
     * @param key The key.
     * @param value The value.
     *
     * @return The previous value associated with the same key, or null if
     *         there was no previous value.
     */
    override fun put(key: SIUnit<K>, value: V): V? {
        // Remove old entries from the map.
        clean()

        // Add the new value into the map.
        return super.put(key, value)
    }

    companion object {
        /**
         * Creates an interpolating tree map buffer that contains two SIUnit types.
         */
        fun <K : SIKey, V : SIKey> createFromSI(
            bufferSize: SIUnit<K>,
            source: Source<SIUnit<K>>,
        ) = InterpolatingTreeMapBuffer<K, SIUnit<V>>({ start, end, t ->
            start.lerp(end, t)
        }, bufferSize, source)

        /**
         * Creats an interpolating tree map buffer from a type that implements [Interpolatable].
         */
        fun <K : SIKey, V : Interpolatable<V>> createFromInterpolatable(
            bufferSize: SIUnit<K>,
            source: Source<SIUnit<K>>,
        ) = InterpolatingTreeMapBuffer<K, V>({ start, end, t ->
            start.interpolate(end, t)
        }, bufferSize, source)
    }
}
