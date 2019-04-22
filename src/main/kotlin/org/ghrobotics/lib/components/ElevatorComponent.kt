package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.threedim.geometry.Pose3d
import org.ghrobotics.lib.mathematics.threedim.geometry.Vector3
import org.ghrobotics.lib.mathematics.units.Length

abstract class ElevatorComponent(
    private val elevatorZero: Vector3
) : MotorComponent<Length>() {

    protected abstract val elevatorKg: Double

    override fun update() {
        arbitraryFeedForward = elevatorKg
        localTransform = Pose3d(
            translation = Vector3(
                elevatorZero.x,
                elevatorZero.y,
                elevatorZero.z + motor.encoder.position
            )
        )
    }

}