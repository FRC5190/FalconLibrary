/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import java.util.ArrayList

class CircularBuffer(private val size: Int) {

    private val buffer: ArrayList<Double> = ArrayList(size)

    private var numElements = 0
    private var sum = 0.0

    // Gets average of all elements
    val average: Double
        get() {
            return if (numElements == 0) {
                0.0
            } else
                sum / numElements
        }

    // Adds an element to the list
    fun add(element: Double) {
        if (numElements > size - 1) {
            sum -= buffer[size - 1]
            buffer.removeAt(size - 1)
            numElements--
        }
        sum += element
        buffer.add(0, element)
        numElements++
    }
}
