/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.units.meters
import org.junit.Test

class GeometryTests {
    @Test
    fun testRectangleContains() {
        val rectangle = Rectangle2d(Translation2d(0.0.meters, 0.0.meters), Translation2d(10.0.meters, 10.0.meters))
        val translation = Translation2d(5.0.meters, 7.0.meters)
        assert(rectangle.contains(translation))
    }
}