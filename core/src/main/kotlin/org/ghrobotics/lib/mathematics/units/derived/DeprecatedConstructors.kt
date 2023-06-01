/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units.derived

@Deprecated("Replaced with Plural version", ReplaceWith("radians"))
val Double.radian
    get() = radians

@Deprecated("Replaced with Plural version", ReplaceWith("degrees"))
val Double.degree
    get() = degrees

@Deprecated("Replaced with Plural version", ReplaceWith("radians"))
val Number.radian
    get() = radians

@Deprecated("Replaced with Plural version", ReplaceWith("degrees"))
val Number.degree
    get() = degrees
