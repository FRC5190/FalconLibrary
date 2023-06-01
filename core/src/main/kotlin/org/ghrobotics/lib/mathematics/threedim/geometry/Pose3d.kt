/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.threedim.geometry

typealias Transform = Pose3d

data class Pose3d(
    val translation: Translation3d = Translation3d.kZero,
    val rotation: Quaternion = Quaternion.kIdentity,
) {

    operator fun minus(other: Pose3d) = this + -other

    operator fun plus(other: Pose3d) =
        Pose3d(
            translation + (other.translation * rotation),
            rotation * other.rotation,
        )

    operator fun unaryMinus(): Pose3d {
        val invertedRotation = -rotation
        return Pose3d((-translation) * invertedRotation, invertedRotation)
    }

    operator fun times(scalar: Double) = Pose3d(
        translation * scalar,
        rotation * scalar,
    )

    operator fun div(scalar: Double) = times(1.0 / scalar)

    infix fun inFrameOfReferenceOf(fieldRelativeOrigin: Pose3d) = (-fieldRelativeOrigin) + this
}
