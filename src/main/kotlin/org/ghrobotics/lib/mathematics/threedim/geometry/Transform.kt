package org.ghrobotics.lib.mathematics.threedim.geometry

/**
 * @param localPosition represents the position relative to the parent
 * @param localRotation represents the rotation relative to the parent
 */
class Transform {

    var localPosition = Vector3.kZero
        private set
    var localRotation = Quaternion.kIdentity
        private set

    internal fun updateLocal(
        localPosition: Vector3 = Vector3.kZero,
        localRotation: Quaternion = Quaternion.kIdentity
    ) {
        this.localPosition = localPosition
        this.localRotation = localRotation
    }

}