package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.threedim.geometry.Pose3d
import org.ghrobotics.lib.mathematics.threedim.geometry.Quaternion
import org.ghrobotics.lib.mathematics.threedim.geometry.Vector3
import org.ghrobotics.lib.mathematics.units.Rotation2d

abstract class ArmComponent(
    val armAxleOffset: Vector3,
    val armRotationAxis: Vector3
) : MotorComponent<Rotation2d>() {

    abstract val armKg: Double

    override fun update() {
        super.update()

        // TODO make this work with any component and not just an elevator
        // TODO make this based on its actual rotation and not local rotation
        var experiencedAcceleration = 9.80665
        val parent = this.parent
        if (parent is ElevatorComponent) {
            experiencedAcceleration += parent.acceleration
        }

        arbitraryFeedForward = armKg * Math.cos(position) * experiencedAcceleration

        transform = Pose3d(
            armAxleOffset,
            Quaternion.fromAxisAngle(position, armRotationAxis)
        )
    }

}

