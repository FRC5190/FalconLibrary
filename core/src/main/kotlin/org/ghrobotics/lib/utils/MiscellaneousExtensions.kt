/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Transform2d

/**
 * Converts a Pose2d to a Transform2d.
 */
fun Pose2d.toTransform(): Transform2d = minus(Pose2d())

/**
 * Converts a Transform2d to a Pose2d.
 */
fun Transform2d.toPose(): Pose2d = Pose2d(translation, rotation)
