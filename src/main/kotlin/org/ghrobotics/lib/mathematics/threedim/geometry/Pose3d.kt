package org.ghrobotics.lib.mathematics.threedim.geometry

typealias Transform = Pose3d

data class Pose3d(
    val translation: Translation3d = Translation3d.kZero,
    val rotation: Quaternion = Quaternion.kIdentity
) {

    operator fun plus(other: Pose3d) =
        Pose3d(
            translation + (other.translation * rotation),
            rotation * other.rotation
        )

}