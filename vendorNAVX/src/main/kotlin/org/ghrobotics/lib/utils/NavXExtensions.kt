/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.geometry.Rotation2d
import org.ghrobotics.lib.utils.Source

fun AHRS.asSource(): Source<Rotation2d> = { Rotation2d.fromDegrees(-fusedHeading.toDouble()) }