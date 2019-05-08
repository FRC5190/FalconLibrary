package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.threedim.geometry.Pose3d
import org.ghrobotics.lib.mathematics.threedim.geometry.Transform
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.units.Length

abstract class ElevatorComponent(
    private val elevatorZero: Translation3d
) : MotorComponent<Length>() {

    protected abstract val elevatorKg: Double

    override fun updateState() {

        arbitraryFeedForward = elevatorKg

        localTransform = Pose3d(
            translation = Translation3d(
                elevatorZero.x,
                elevatorZero.y,
                elevatorZero.z + position
            )
        )

        localVelocityTransform = Transform(
            Translation3d(0.0, 0.0, velocity)
        )

        super.updateState()
    }

}